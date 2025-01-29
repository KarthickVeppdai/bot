package com.example.rag.rabbitmq;


import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "input-queue")
public class InputMsgHandler {

    @RabbitHandler
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}

