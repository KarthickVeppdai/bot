package com.example.rag.rabbitmq;



import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void putToInputQueue(String orderId) {
        rabbitTemplate.convertAndSend(RabbitMqConfiguration.CHAT_DATA_EXCHANGE,RabbitMqConfiguration.ROUTING_KEY_CHAT, orderId);
        System.out.println("Sent message: " + orderId);
    }


    public void putToSendQueue(String orderId) {
        rabbitTemplate.convertAndSend(RabbitMqConfiguration.CHAT_DATA_EXCHANGE,RabbitMqConfiguration.ROUTING_KEY_CHAT, orderId);
        System.out.println("Sent message: " + orderId);
    }

}
