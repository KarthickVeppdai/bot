package com.example.rag.xtras;


import dev.langchain4j.agent.tool.Tool;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component
public class ChatBotTools {

    @Tool("Checks class selected or not by user")
    public String getClassSelected() {
        System.out.println("Checking Class selected or not!!!");

        System.out.println("==");
        sendClass();

        return "Ask user to Select class";
    }

    @Tool("Sets class for User")
    public String setClass() {
        System.out.println("Setted Class");

        System.out.println("==");

        return "Class 8";
    }
    @Tool("Class selection options")
    public String sendOptionsForClassSelection() {
        System.out.println("Sending Class Options");
        sendClass();
        System.out.println("==");

        return "Ask user to Select class";
    }


    public String sendClass() {
        System.out.println("Inside Welcome");
        String result,body;
        String tok = "Bearer EAAQjk2eLHbIBO3GGJ9c0O1Nh5L6pWOQOcDsqo9DXeE00VOTlshSS6ZCj0u8LNG5WBGZCBZARQ4WLKkOtae5l5EtU6N9TbSdAQ8R7AV8kf0TBkmNlYpeHkWRr5QRLttbAYtZCkQv6PAzMnwCsBT7q77ljcvaJo4BojPiqfon8ZAPBoQsMrbX5kFzSpYyu8ButdbzbAxQIeTnTMMZCUEpzx3CMWLnb8ZD";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost("https://graph.facebook.com/v19.0/284845538036793/messages");
        postRequest.addHeader("Authorization", tok);
        postRequest.addHeader("content-type", "application/json");

        String classoptions= """
                                1. Class 8
                                2. Class 9
                                3. Class 10
                """;

        JSONObject object= new JSONObject();
        object.put("messaging_product","whatsapp");
        object.put("recipient_type","individual");
        object.put("to","919543249890");
        object.put("type","text");
        JSONObject object1= new JSONObject();
        object1.put("preview_url","false");
        object1.put("body",classoptions);
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
}
