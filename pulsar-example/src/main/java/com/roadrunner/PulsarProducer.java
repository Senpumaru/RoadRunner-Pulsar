package com.roadrunner;

import org.apache.pulsar.client.api.*;
import lombok.extern.slf4j.Slf4j;;

@Slf4j
public class PulsarProducer implements AutoCloseable {
    private final PulsarClient client;
    private final Producer<byte[]> producer;

    public PulsarProducer(String serviceUrl, String topic) throws PulsarClientException {
        client = PulsarClient.builder()
                .serviceUrl(serviceUrl)
                .build();

        producer = client.newProducer()
                .topic(topic)
                .create();
    }

    public void sendMessage(String message) throws PulsarClientException {
        producer.send(message.getBytes());
        log.info("Sent message: {}", message);
    }

    @Override
    public void close() throws Exception {
        producer.close();
        client.close();
    }

    public static void main(String[] args) {
        try (PulsarProducer producer = new PulsarProducer("pulsar://pulsar:6650", "my-topic")) {
            producer.sendMessage("Hello, Pulsar!");
        } catch (Exception e) {
            log.error("Error in Pulsar producer", e);
        }
    }
}