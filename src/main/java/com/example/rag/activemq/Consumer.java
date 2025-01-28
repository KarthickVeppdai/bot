package com.example.rag.activemq;


import com.example.rag.httpclient.GetAndSetContext;
import com.example.rag.models.CustomMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Consumer {

    @Autowired
    GetAndSetContext getAndSetContext;
    @JmsListener(destination = "message")
    public void receiveMessage(CustomMessage message) {
        try{
            System.out.println(getAndSetContext.getContextForUser(message.getMessage_id()).getAge());
        } catch (Exception e) {
            throw new RuntimeException("Problem in receiving message from Active MQ");
        }
    }
}
