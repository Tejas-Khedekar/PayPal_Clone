package com.paypal.transaction_service.service;

import com.paypal.transaction_service.entity.Transaction;
import com.paypal.transaction_service.kafka.KafkaEventProducer;
import com.paypal.transaction_service.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final KafkaEventProducer kafkaEventProducer;
    private final JsonMapper objectMapper;

    public TransactionServiceImpl(TransactionRepository repository,
                                  KafkaEventProducer kafkaEventProducer,
                                  JsonMapper objectMapper) {
        this.repository = repository;
        this.kafkaEventProducer = kafkaEventProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        System.out.println("Entered createTransaction");

        Long senderId = transaction.getSenderId();
        Long receiverId = transaction.getReceiverId();
        Double amount = transaction.getAmount();

        transaction.setSenderId(senderId);
        transaction.setReceiverId(receiverId);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("SUCCESS");

        System.out.println("Incoming Transaction Object: " + transaction);

        Transaction saved = repository.save(transaction);
        System.out.println("Saved Transaction from DB: " + saved);

        try {
            String eventPayload = objectMapper.writeValueAsString(saved);
            String key = String.valueOf(saved.getId());

            kafkaEventProducer.sendTransactionEvent(key, eventPayload);
            System.out.println("Kafka message sent");
        } catch (Exception e) {
            System.err.println("Failed to send Kafka event: " + e.getMessage());
            e.printStackTrace();
        }

        return saved;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }
}