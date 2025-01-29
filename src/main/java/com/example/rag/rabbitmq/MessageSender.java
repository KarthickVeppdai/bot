package com.example.rag.rabbitmq;



import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String orderId) {
        rabbitTemplate.convertAndSend(RabbitMqConfiguration.GPS_DATA_EXCHANGE,RabbitMqConfiguration.ROUTING_KEY_GPS, orderId);
        System.out.println("Sent message: " + orderId);
    }

}
