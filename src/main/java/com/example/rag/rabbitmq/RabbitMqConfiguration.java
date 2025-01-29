package com.example.rag.rabbitmq;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfiguration {


    public static final String CHAT_DATA_EXCHANGE = "chat-data-exchange";
    public static final String INPUT_QUEUE = "input-queue";
    public static final String SEND_QUEUE = "send-queue";
    public static final String ROUTING_KEY_CHAT = "chat";

    @Bean
    public DirectExchange gpsDirectExchange() {
       return new DirectExchange(CHAT_DATA_EXCHANGE);
   }

    @Bean
    public Queue input_queue() {
        return new Queue(INPUT_QUEUE); // true for durable queue
    }

    @Bean
    public Queue send_queue() {
        return new Queue(INPUT_QUEUE); // true for durable queue
    }

    @Bean
    public Binding inputBinding() {
        return BindingBuilder.bind(input_queue()).to(gpsDirectExchange()).with(ROUTING_KEY_CHAT);
    }
    @Bean
    public Binding sendBinding() {
        return BindingBuilder.bind(send_queue()).to(gpsDirectExchange()).with(ROUTING_KEY_CHAT);
    }

}
