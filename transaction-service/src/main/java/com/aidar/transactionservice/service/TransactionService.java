package com.aidar.transactionservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aidar.transactionservice.entity.Transaction;
import com.aidar.transactionservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    public TransactionService(TransactionRepository transactionRepository, ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void processTransactionMessage(String messageJson) throws Exception {
        JsonNode json = objectMapper.readTree(messageJson);
        String externalId = json.get("externalId").asText();
        String payload = json.toString();

        // Идемпотентность: проверяем, не обрабатывали ли уже
        var existing = transactionRepository.findByExternalId(externalId);
        if (existing.isPresent()) {
            log.info("Transaction with externalId={} already processed", externalId);
            return;
        }

        Transaction tx = new Transaction(externalId, payload);
        tx.setStatus("PROCESSING");
        tx = transactionRepository.save(tx);

        // Имитация бизнес-логики (можно заменить на реальную)
        simulateProcessing();

        tx.setStatus("COMPLETED");
        tx.setProcessedAt(Instant.now());
        transactionRepository.save(tx);

        log.info("Transaction {} completed", tx.getId());
    }

    private void simulateProcessing() throws InterruptedException {
        Thread.sleep(1000); // имитация работы
    }
}