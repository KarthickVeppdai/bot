package com.example.rag.chat_controller;


import com.example.rag.models.CustomMessage;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class MessageService implements Serializable {
    public CustomMessage buildMessageFromUser(JsonNode message){
      try {
          return CustomMessage.builder()
                  .from_or_id(message.findValuesAsText("from").get(0))
                  .message_id(message.findValuesAsText("id").get(0))
                  .msg_body(message.findValuesAsText("body").get(0))
                  .message_type(message.findValuesAsText("type").get(0))
                  .build();
      }
      catch (Exception e)
      {
          throw new RuntimeException("some issue in JSON Node processing");
      }
    }
}
