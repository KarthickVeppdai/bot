package com.example.rag.xtras;


import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;


@Configuration
public class PDFUtility {

    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }
    @Bean
    public PgVectorEmbeddingStore pgVectorEmbeddingStore()
    {
        return new PgVectorEmbeddingStore("127.0.0.1",5433,"postgres","postgres","postgres","book",384,false,1000,true,false);
    }
    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor() {
        DocumentSplitter splitter = DocumentSplitters.recursive(
                300,
                20,
                new OpenAiTokenizer(GPT_3_5_TURBO));
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel())
                .embeddingStore(pgVectorEmbeddingStore())
                .build();
    }
    @Bean
    public ConversationalRetrievalChain conversationalRetrievalChain() {
        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(OpenAiChatModel.withApiKey("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"))
                .retriever(EmbeddingStoreRetriever.from(pgVectorEmbeddingStore(), embeddingModel()))
                .build();
    }

}
