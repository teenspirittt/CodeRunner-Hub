package com.teenspirit.coderunnerhub.util;

import com.teenspirit.coderunnerhub.dto.TestResultDTO;
import com.teenspirit.coderunnerhub.util.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);


    @Value("${rabbitmq.exchange.name}")
    private  String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private  String routingKey;


    @Autowired
    public MessageSender(RabbitTemplate rabbitTemplate, RabbitMQConfig rabbitMQConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setMessageConverter(rabbitMQConfig.converter());
    }

    public void sendMessage(TestResultDTO testResultDTO) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, testResultDTO);
        LOGGER.info(String.format("Message sent ->%s", testResultDTO));
    }
}
