package com.paypal.user_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventConsumer {

    @KafkaListener(topics = "txn-initiates", groupId = "user-service-group")
    public void consumeTransactionEvent(String message) {
        try {
            System.out.println("Received transaction event in user-service -> " + message);
        } catch (Exception e) {
            System.err.println("Failed to process transaction event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}