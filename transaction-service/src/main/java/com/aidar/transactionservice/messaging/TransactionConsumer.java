package com.aidar.transactionservice.messaging;

import com.aidar.transactionservice.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    private final TransactionService transactionService;

    public TransactionConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consume(String messageJson) throws Exception {
        log.info("Received message: {}", messageJson);
        try {
            transactionService.processTransactionMessage(messageJson);
        } catch (Exception e) {
            log.error("Failed to process message: {}", messageJson, e);
            // Здесь можно отправить в DLQ — добавим позже
            throw e; // повторная попытка
        }
    }
}