package com.example.rag.rabbitmq;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfiguration {


    public static final String GPS_DATA_EXCHANGE = "gps-data-exchange";
    public static final String GPS_QUEUE = "gps-queue";
    public static final String ROUTING_KEY_GPS = "gps";

    @Bean
    public DirectExchange gpsDirectExchange() {
       return new DirectExchange(GPS_DATA_EXCHANGE);
   }

    @Bean
    public Queue queue() {
        return new Queue(GPS_QUEUE); // true for durable queue
    }

    @Bean
    public Binding gpsBinding() {
        return BindingBuilder.bind(queue()).to(gpsDirectExchange()).with(ROUTING_KEY_GPS);
    }

}
