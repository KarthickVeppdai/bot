package com.example.rag.rabbitmq;


import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "send-queue")
public class OutMsgSender {


    @RabbitHandler
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}
