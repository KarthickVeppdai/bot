package com.example.rag.ebox_controller;

import com.example.rag.xtras.Class8Utility;
import com.example.rag.xtras.Class9Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data-load")
public class DataLoader {

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
}
