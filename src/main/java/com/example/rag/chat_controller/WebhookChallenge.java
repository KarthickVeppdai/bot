package com.example.rag.chat_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/challenge")
public class WebhookChallenge {

    @GetMapping("/webhook")
    public String test1(@RequestParam(value = "hub.challenge", defaultValue = "0")String challange){
        System.out.println("body");
        return challange;
    }


}
