package com.roadrunner;

import org.apache.pulsar.client.api.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PulsarStressTest implements AutoCloseable {
    private final PulsarClient client;
    private final Producer<byte[]> producer;
    private final Random random = new Random();

    public PulsarStressTest(String serviceUrl, String topic) throws PulsarClientException {
        client = PulsarClient.builder()
                .serviceUrl(serviceUrl)
                .build();

        producer = client.newProducer()
                .topic(topic)
                .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS)
                .batchingMaxMessages(1000)
                .batchingMaxBytes(128 * 1024)
                .compressionType(CompressionType.LZ4)
                .create();
    }

    // private void sendMessage(String message) throws PulsarClientException {
    //     producer.send(message.getBytes());
    // }

    private void sendMessageAsync(String message) {
        producer.sendAsync(message.getBytes())
            .thenAccept(msgId -> {
                // Optionally log or count successful sends
            })
            .exceptionally(ex -> {
                log.error("Failed to send message", ex);
                return null;
            });
    }

    private String generateRandomMessage(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (random.nextInt(26) + 'a'));
        }
        return sb.toString();
    }

    // public void runStressTest(int numberOfMessages) throws PulsarClientException {
    //     long startTime = System.currentTimeMillis();
    //     for (int i = 0; i < numberOfMessages; i++) {
    //         String message = generateRandomMessage(50);
    //         sendMessage(message);
    //         if (i % 10000 == 0) {
    //             log.info("Sent {} messages", i);
    //         }
    //     }
    //     long endTime = System.currentTimeMillis();
    //     log.info("Sent {} messages in {} ms", numberOfMessages, endTime - startTime);
    // }

    public void runStressTest(int numberOfMessages) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfMessages; i++) {
            String message = generateRandomMessage(50);
            sendMessageAsync(message);
            if (i % 100000 == 0) {
                log.info("Sent {} messages", i);
            }
        }
        
        try {
            // Wait for all async operations to complete
            producer.flush();
        } catch (PulsarClientException e) {
            log.error("Error flushing producer", e);
        }
        
        long endTime = System.currentTimeMillis();
        double duration = (endTime - startTime) / 1000.0;
        double rate = numberOfMessages / duration;
        log.info("Sent {} messages in {} seconds. Rate: {} msg/s", numberOfMessages, duration, String.format("%.2f", rate));
    }

    @Override
    public void close() throws Exception {
        if (producer != null) {
            producer.close();
        }
        if (client != null) {
            client.close();
        }
    }

    public static void main(String[] args) {
        try (PulsarStressTest stressTest = new PulsarStressTest("pulsar://pulsar:6650", "stress-test-topic")) {
            stressTest.runStressTest(1_000_000);
        } catch (PulsarClientException e) {
            log.error("Error creating Pulsar stress test", e);
        } catch (Exception e) {
            log.error("Unexpected error in Pulsar stress test", e);
        }
    }
}