package com.mockapi.server.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    private final CachingConnectionFactory connectionFactory;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    public RabbitMQService(RabbitTemplate rabbitTemplate, CachingConnectionFactory connectionFactory) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionFactory = connectionFactory;
    }

    public void sendMessageToQueue(String queueName, String message, String virtualHost) {
        sendMessageToQueue(queueName, message, virtualHost, true);
    }

    public void sendMessageToQueue(String queueName, String messageContent, String virtualHost, boolean asJson) {
        try {
            if (virtualHost != null && !virtualHost.isEmpty()) {
                setVirtualHost(virtualHost);
            }

            if (asJson) {
                // Send as JSON with proper content-type
                MessageProperties properties = new MessageProperties();
                properties.setContentType("application/json");
                properties.setContentEncoding("UTF-8");
                Message message = new Message(messageContent.getBytes("UTF-8"), properties);
                rabbitTemplate.send(queueName, message);
            } else {
                // Send as plain text
                rabbitTemplate.convertAndSend(queueName, messageContent);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to queue: " + queueName, e);
        }
    }

    public void sendMessageToExchange(String exchange, String routingKey, String message, String virtualHost) {
        sendMessageToExchange(exchange, routingKey, message, virtualHost, true);
    }

    public void sendMessageToExchange(String exchange, String routingKey, String messageContent, String virtualHost, boolean asJson) {
        try {
            if (virtualHost != null && !virtualHost.isEmpty()) {
                setVirtualHost(virtualHost);
            }

            if (asJson) {
                // Send as JSON with proper content-type
                MessageProperties properties = new MessageProperties();
                properties.setContentType("application/json");
                properties.setContentEncoding("UTF-8");
                Message message = new Message(messageContent.getBytes("UTF-8"), properties);
                rabbitTemplate.send(exchange, routingKey, message);
            } else {
                // Send as plain text
                rabbitTemplate.convertAndSend(exchange, routingKey, messageContent);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to exchange: " + exchange, e);
        }
    }

    private void setVirtualHost(String virtualHost) {
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.resetConnection();
    }
}

