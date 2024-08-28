package com.roadrunner;

import org.apache.pulsar.client.api.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImprovedPulsarResponder implements AutoCloseable {
    private final PulsarClient client;
    private final Consumer<byte[]> consumer;
    private final Producer<byte[]> producer;
    private final String topic;

    public ImprovedPulsarResponder(String serviceUrl, String topic) throws PulsarClientException {
        this.topic = topic;
        client = PulsarClient.builder().serviceUrl(serviceUrl).build();

        consumer = client.newConsumer()
                .topic(topic)
                .subscriptionName("responder-subscription")
                .subscriptionType(SubscriptionType.Shared)
                .subscribe();

        producer = client.newProducer()
                .topic(topic)
                .create();
    }

    public void start() throws PulsarClientException {
        while (true) {
            Message<byte[]> msg = consumer.receive();
            try {
                String requestId = msg.getProperty("request-id");
                String request = new String(msg.getData());
                log.info("Received request: {} with ID: {}", request, requestId);

                // Process the request (in this case, just echo it back)
                String response = "Processed: " + request;

                producer.newMessage()
                        .property("request-id", requestId)
                        .value(response.getBytes())
                        .send();

                consumer.acknowledge(msg);
            } catch (Exception e) {
                log.error("Error processing message", e);
                consumer.negativeAcknowledge(msg);
            }
        }
    }

    @Override
    public void close() throws Exception {
        consumer.close();
        producer.close();
        client.close();
    }

    public static void main(String[] args) {
        try (ImprovedPulsarResponder responder = new ImprovedPulsarResponder("pulsar://pulsar:6650", "request-reply-topic-v2")) {
            responder.start();
        } catch (Exception e) {
            log.error("Error in Pulsar responder", e);
        }
    }
}