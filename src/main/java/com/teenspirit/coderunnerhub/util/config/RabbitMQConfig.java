package com.teenspirit.coderunnerhub.util.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;
    private final String EXCHANGE_NAME = "task-exchange";
    private final String QUEUE_NAME = "task_queue";
    private final String ROUTING_KEY = "task-routing-key";


    @Bean
    public Queue taskQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue taskQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(taskQueue).to(directExchange).with(ROUTING_KEY);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitmqHost);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setExchange(EXCHANGE_NAME);
        rabbitTemplate.setRoutingKey(ROUTING_KEY);
        return rabbitTemplate;
    }

    public String getExchangeName() {
        return EXCHANGE_NAME;
    }

    public String getQueueName() {
        return QUEUE_NAME;
    }

    public String getRoutingKey() {
        return ROUTING_KEY;
    }
}