package com.example.rag.activemq;


import com.example.rag.models.CustomMessage;
import org.springframework.jms.core.JmsOperations;
import org.springframework.stereotype.Component;

@Component
public class Producer {


    private final JmsOperations jmsTemplate;

    public Producer(JmsOperations jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void produceMessage(CustomMessage customMessage) {
        try{
            jmsTemplate.convertAndSend("message", customMessage);
        } catch (Exception e) {
            throw new RuntimeException("Cannot send message to the Queue");
        }
    }
}
