package com.example.rag;

import com.example.rag.xtras.AgentChat;
import com.example.rag.xtras.ChatBotTools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/aiservice")
public class AiApiService {
    @PostMapping("/apicall")
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
}
