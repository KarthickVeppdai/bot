package com.example.rag.chat_controller;


import com.example.rag.activemq.Producer;
import com.example.rag.xtras.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/input")
public class IncomingChatController {

    @Autowired
    Producer producer;

    @Autowired
    MessageService messageService;

    @PostMapping("/webhook")
    public ResponseEntity webhooktesting() throws JsonProcessingException {

        System.out.println("Inside test");
        String result,body;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet postRequest = new HttpGet("http://demo4010504.mockable.io/data");
        CloseableHttpResponse output = null;
        try {
            output = httpClient.execute(postRequest);
            result = EntityUtils.toString(output.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configOverride(Boolean.class).setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
        JsonNode node = mapper.readTree(result);
        Boolean has_entry = node.hasNonNull("entry");
        JsonNode message,statuses,value,changes,button,list;
        List<String> message_type,status_type;

        String msg_body,from,msg_id,button_text,list_text,recipient_id,interactive_type;

        Message message_wta = new Message();
        if(has_entry !=null){
            changes = node.findValue("changes");
            if(changes !=null){
                message = node.findValue("messages");
                statuses = node.findValue("statuses");
                value = node.findValue("value");

                if(message !=null)
                {

                    message_type=message.findValuesAsText("type");
                    switch (message_type.isEmpty()?"nothing":message_type.get(0)) {
                        case "text" :
                            // till now not written --Product Inquiry Messages and Received Message Triggered by Click to WhatsApp Ads should be avoided
                            // write for exclusion case
                            try {
                                producer.produceMessage(messageService.buildMessageFromUser(message));
                                // send read recipt using seperate queue or sevice class
                            }catch (Exception e)
                            {   // call that function --send error message to user for "something went wrong"
                                return ResponseEntity.ok().build();
                            }
                            break;
                        case "reaction", "image", "sticker", "unknown","unsupported":
                            // send read recipt to that message id
                            // call send message function Ask for proper resoponse accoring to chat contxt
                            break;
                        case "button" :
                            from=message.findValuesAsText("from").get(0);
                            msg_id=message.findValuesAsText("id").get(0);
                            button = node.findValue("button");
                            button_text = button.findValuesAsText("text").get(0);
                            message_wta = new Message(from,msg_id,button_text,message_type.get(0));
                            break;
                        case "interactive","list_reply","button_reply":
                            from=message.findValuesAsText("from").get(0);
                            msg_id=message.findValuesAsText("id").get(0);
                            list = node.findValue("interactive");
                            list_text= list.findValuesAsText("title").get(0);
                            interactive_type= list.findValuesAsText("type").get(0);
                            message_wta = new Message(from,msg_id,list_text,interactive_type);
                            // Write for 2 constions 1`.Received Answer to Reply Button and 2. Received Answer From List Message
                            break;
                        default :
                            // Location  and Contact Messages are avoided and no case is written ,handled by default.
                            // send read recipt to that message id
                            // call send message function Ask for proper resoponse accoring to chat contxt
                             return ResponseEntity.ok().build();
                    };
                }
                else if(statuses !=null)
                {
                    status_type=statuses.findValuesAsText("status");
                    switch (status_type.isEmpty()?"nothing":status_type.get(0)) {
                        case "sent" :
                            recipient_id = statuses.findValuesAsText("recipient_id").get(0);
                            System.out.println(recipient_id+"sent");
                            break;
                        case "read" :
                            recipient_id = statuses.findValuesAsText("recipient_id").get(0);
                            System.out.println(recipient_id+"read");
                            break;
                        case "failed" :
                            recipient_id = statuses.findValuesAsText("recipient_id").get(0);
                            System.out.println(recipient_id+"faoled");
                            break;
                        case "delivered":
                            recipient_id = statuses.findValuesAsText("recipient_id").get(0);
                            System.out.println(recipient_id+"delivered");
                            break;
                        default :
                            recipient_id = statuses.findValuesAsText("recipient_id").get(0);
                            System.out.println(recipient_id+"other");
                            // only 200 response, no read recipt
                            return ResponseEntity.ok().build();
                    };
                }
                else {// If it is no message and no status
                    // only 200 response, no read recipt
                    // send "someting went wrong" to user
                    return ResponseEntity.ok().build();
                }
            }
            else {//if changes object not found
                // only 200 response, no read recipt
                // send "someting went wrong" to user
                return ResponseEntity.ok().build();
            }
        }//if entry object not found
        //only 200 response, no read recipt
        // send "someting went wrong" to user
        return ResponseEntity.ok().build();
    }

}
