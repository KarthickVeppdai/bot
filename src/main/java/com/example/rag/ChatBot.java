package com.example.rag;


import com.example.rag.models.CustomMessage;
import com.example.rag.activemq.Producer;
import com.example.rag.service.MessageService;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/load")
public class ChatBot {

    @Autowired
    EmbeddingStoreIngestor embeddingStoreIngestor;

    @Autowired
    PgVectorEmbeddingStore pgVectorEmbeddingStore;

    @Autowired
    EmbeddingModel embeddingModel;

    @Autowired
    private Class9Utility class9Utility;
    @Autowired
    private Class8Utility class8Utility;

    @Autowired
    private OllamaChatClient chatClient;

    @Autowired
    Producer producer;

    @Autowired
    MessageService messageService;


    @GetMapping("/pdf")
    public String load() {
        class8Utility.loadPDF();
        return class9Utility.loadPDF();

    }

    @PostMapping("/msg")
    public String msg(@RequestParam String body) throws JsonProcessingException {


        System.out.println(body);

        // return class8Utility.msg(body);
        return "ok";
    }

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








        @PostMapping("/test")
        public void test(@RequestParam String body)
        {
            try {
                ChatLanguageModel model = OpenAiChatModel.builder()
                        .apiKey("sk-proj-mie036BgDG0xFMxegCrgT3BlbkFJlCbtd8DMzD90RGMfnO87")
                        .build();
                AgentChat assistant = AiServices.builder(AgentChat.class)
                        .chatLanguageModel(model)
                        .tools(new ChatBotTools())
                        .chatMemory(MessageWindowChatMemory.withMaxMessages(5))
                        .build();
                String answer = assistant.answer(body);
                System.out.println(answer);
            }catch(Exception e){
                System.out.println("Error");
            }
        }



    @GetMapping("/webhook")
    public String test1(@RequestParam(value = "hub.challenge", defaultValue = "0")String challange){
        System.out.println("body");
        return challange;
    }

    public String sendWelcome(String welocme) {
        System.out.println("Inside Welcome");
        String result,body;
        String tok = "Bearer EAAQjk2eLHbIBO5ApcdfZBjdS1QDtQmpatmV2ZAVly9p92HZC4Jpco47KZBqhhj9GKTOCCZBLBn4ZBGnLETGXUVglhtTkcQjqos4PGtDPPO3kDU9ymZBDe8Leyp4h10Rdn5CrgZCozkpCpCZCDcZCSpaP6q7g9HFGc6Yn7YeNFAaUqucZCklHLwGx1k0UoSR8AdHupheHfRzZBp8O7OUTB6ADAZBTk83tLTDnG";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost("https://graph.facebook.com/v19.0/284845538036793/messages");
        postRequest.addHeader("Authorization", tok);
        postRequest.addHeader("content-type", "application/json");
        JSONObject object= new JSONObject();
        object.put("messaging_product","whatsapp");
        object.put("recipient_type","individual");
        object.put("to","919543249890");
        object.put("type","text");
        JSONObject object1= new JSONObject();
        object1.put("preview_url","false");
        object1.put("body",welocme);
        object.put("text",object1);
        StringEntity userEntity = null;
        try {
            userEntity = new StringEntity(object.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        postRequest.setEntity(userEntity);
        CloseableHttpResponse output = null;
        try {
            output = httpClient.execute(postRequest);
            result = EntityUtils.toString(output.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public String sendClass() {
        String result,body;
        String tok = "Bearer EAAQjk2eLHbIBO3GGJ9c0O1Nh5L6pWOQOcDsqo9DXeE00VOTlshSS6ZCj0u8LNG5WBGZCBZARQ4WLKkOtae5l5EtU6N9TbSdAQ8R7AV8kf0TBkmNlYpeHkWRr5QRLttbAYtZCkQv6PAzMnwCsBT7q77ljcvaJo4BojPiqfon8ZAPBoQsMrbX5kFzSpYyu8ButdbzbAxQIeTnTMMZCUEpzx3CMWLnb8ZD";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost("https://graph.facebook.com/v19.0/284845538036793/messages");
        postRequest.addHeader("content-type", "application/json");
        postRequest.addHeader("Authorization", tok);
        body="{\n    \"messaging_product\": \"whatsapp\",\n    \"to\": \"919543249890\",\n    \"type\": \"template\",\n    \"template\": {\n        \"name\": \"class\",\n        \"language\": {\n            \"code\": \"en_US\"\n        }\n    }\n}";
        StringEntity userEntity = null;
        try {
            userEntity = new StringEntity(body.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        postRequest.setEntity(userEntity);

        CloseableHttpResponse output = null;
        try {
            output = httpClient.execute(postRequest);
            result = EntityUtils.toString(output.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
