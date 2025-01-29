package com.example.rag.send_util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SendMessage {


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
    public String sendTemplate() {
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
