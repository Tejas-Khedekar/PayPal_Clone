package com.paypal.transaction_service.kafka;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaEventProducer {

    private static final String TOPIC = "txn-initiates";
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionEvent(String key, String message) {
        System.out.println("Sending to Kafka -> Topic: " + TOPIC + ", Key: " + key + ", Message: " + message);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, key, message);

        future.thenAccept(result -> {
            RecordMetadata metadata = result.getRecordMetadata();
            System.out.println("Kafka message sent successfully! Topic: " + metadata.topic()
                    + ", Partition: " + metadata.partition()
                    + ", Offset: " + metadata.offset());
        }).exceptionally(ex -> {
            System.out.println("Failed to send Kafka message: " + ex.getMessage());
            return null;
        });
    }
}