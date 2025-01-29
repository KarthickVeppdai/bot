package com.example.rag.rabbitmq;


import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "gps-queue")
public class MessageReceiver {

    public static final String QUEUE_NAME = "gps-queue";
    public static final String GPS_DATA_EXCHANGE = "gps-data-exchange";



    @RabbitHandler
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}

