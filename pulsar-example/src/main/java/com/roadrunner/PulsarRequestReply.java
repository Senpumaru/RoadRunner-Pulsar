package com.roadrunner;

import org.apache.pulsar.client.api.*;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PulsarRequestReply implements AutoCloseable {
    private final PulsarClient client;
    private final Producer<byte[]> producer;
    private final Consumer<byte[]> consumer;
    private final String topic;

    public PulsarRequestReply(String serviceUrl, String topic) throws PulsarClientException {
        this.topic = topic;
        this.client = PulsarClient.builder().serviceUrl(serviceUrl).build();
        
        this.producer = client.newProducer()
            .topic(topic)
            .create();
        
        this.consumer = client.newConsumer()
            .topic(topic)
            .subscriptionName("request-reply-subscription")
            .subscribe();
    }

    public void sendMessage(String message) throws PulsarClientException {
        producer.send(message.getBytes());
        log.info("Sent message: {}", message);
    }

    public String receiveMessage(int timeoutMs) throws PulsarClientException {
        Message<byte[]> msg = consumer.receive(timeoutMs, TimeUnit.MILLISECONDS);
        if (msg != null) {
            String receivedMessage = new String(msg.getData());
            consumer.acknowledge(msg);
            log.info("Received message: {}", receivedMessage);
            return receivedMessage;
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        producer.close();
        consumer.close();
        client.close();
    }

    public static void main(String[] args) {
        try (PulsarRequestReply client = new PulsarRequestReply("pulsar://pulsar:6650", "request-reply-topic")) {
            client.sendMessage("Hello, Pulsar!");
            String response = client.receiveMessage(5000);
            if (response != null) {
                log.info("Received response: {}", response);
            } else {
                log.warn("No response received within timeout");
            }
        } catch (Exception e) {
            log.error("Error in Pulsar request-reply", e);
        }
    }
}