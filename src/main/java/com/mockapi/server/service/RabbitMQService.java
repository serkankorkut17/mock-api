package com.mockapi.server.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    private final CachingConnectionFactory connectionFactory;
    private final List<QueueMessage> sentMessages = new ArrayList<>();

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

            // Track sent message
            sentMessages.add(new QueueMessage(queueName, virtualHost, messageContent, asJson));
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

            // Track sent message
            sentMessages.add(new QueueMessage(exchange + " (Routing: " + routingKey + ")", virtualHost, messageContent, asJson));
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to exchange: " + exchange, e);
        }
    }

    private void setVirtualHost(String virtualHost) {
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.resetConnection();
    }

    public List<QueueMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clearSentMessages() {
        sentMessages.clear();
    }

    // Inner class to represent a message
    public static class QueueMessage {
        private final String destination;
        private final String virtualHost;
        private final String content;
        private final boolean isJson;
        private final long timestamp;

        public QueueMessage(String destination, String virtualHost, String content, boolean isJson) {
            this.destination = destination;
            this.virtualHost = virtualHost;
            this.content = content;
            this.isJson = isJson;
            this.timestamp = System.currentTimeMillis();
        }

        public String getDestination() {
            return destination;
        }

        public String getVirtualHost() {
            return virtualHost;
        }

        public String getContent() {
            return content;
        }

        public boolean isJson() {
            return isJson;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}

