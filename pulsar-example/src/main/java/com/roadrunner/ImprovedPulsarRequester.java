package com.roadrunner;
import org.apache.pulsar.client.api.*;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ImprovedPulsarRequester implements AutoCloseable {
    private final PulsarClient client;
    private final Producer<byte[]> producer;
    private final Consumer<byte[]> consumer;
    private final String topic;
    private final Map<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    public ImprovedPulsarRequester(String serviceUrl, String topic) throws PulsarClientException {
        this.topic = topic;
        client = PulsarClient.builder().serviceUrl(serviceUrl).build();

        producer = client.newProducer()
                .topic(topic)
                .create();

        consumer = client.newConsumer()
                .topic(topic)
                .subscriptionName("requester-subscription-" + UUID.randomUUID().toString())
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(this::handleResponse)
                .subscribe();
    }

    public CompletableFuture<String> sendRequest(String message) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<String> responseFuture = new CompletableFuture<>();
        pendingRequests.put(requestId, responseFuture);

        try {
            producer.newMessage()
                    .property("request-id", requestId)
                    .value(message.getBytes())
                    .send();
            log.info("Sent request with ID: {}", requestId);
        } catch (PulsarClientException e) {
            pendingRequests.remove(requestId);
            responseFuture.completeExceptionally(e);
            log.error("Failed to send request", e);
        }

        return responseFuture;
    }

    private void handleResponse(Consumer<byte[]> consumer, Message<byte[]> msg) {
        String requestId = msg.getProperty("request-id");
        CompletableFuture<String> future = pendingRequests.remove(requestId);
        if (future != null) {
            String response = new String(msg.getData());
            future.complete(response);
        }
        try {
            consumer.acknowledge(msg);
        } catch (PulsarClientException e) {
            log.error("Failed to acknowledge message", e);
        }
    }

    @Override
    public void close() throws Exception {
        producer.close();
        consumer.close();
        client.close();
    }

    public static void main(String[] args) {
        try (ImprovedPulsarRequester requester = new ImprovedPulsarRequester("pulsar://pulsar:6650", "request-reply-topic-v2")) {
            CompletableFuture<?>[] futures = new CompletableFuture[10];
            for (int i = 0; i < 10; i++) {
                final int index = i;
                futures[i] = requester.sendRequest("Request " + i)
                    .thenAccept(response -> log.info("Received response for request {}: {}", index, response));
            }
            CompletableFuture.allOf(futures).join();
        } catch (Exception e) {
            log.error("Error in Pulsar requester", e);
        }
    }
}