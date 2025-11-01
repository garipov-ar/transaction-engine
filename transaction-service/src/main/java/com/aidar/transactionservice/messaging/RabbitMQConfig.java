package com.aidar.transactionservice.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "transaction.exchange";
    public static final String QUEUE_NAME = "transaction.process";
    public static final String DLQ_NAME = "transaction.process.dlq";
    public static final String ROUTING_KEY = "transaction.process";

    // Основной exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // DLX — exchange для мёртвых писем
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(EXCHANGE_NAME + ".dlx");
    }

    // DLQ — очередь для проваленных сообщений
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    // Привязка DLQ к DLX
    @Bean
    public Binding dlqBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(ROUTING_KEY);
    }

    // Основная очередь с настройками DLQ и retry
    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME + ".dlx")     // куда слать проваленные
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY)             // с каким routing key
                .withArgument("x-message-ttl", 5000)                                // TTL 5 сек (для демо)
                .withArgument("x-max-length", 1000)                                 // лимит очереди
                .build();
    }

    // Привязка основной очереди к exchange
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}