package com.example.rag.xtras;

import com.example.rag.RagApplication;
import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static java.util.stream.Collectors.joining;

@Service
public class Class8Utility {

    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    public PgVectorEmbeddingStore pgVectorEmbeddingStore()
    {
        return new PgVectorEmbeddingStore("127.0.0.1",5433,"postgres","postgres","postgres","class8",384,false,1000,true,false);
    }

    public EmbeddingStoreIngestor embeddingStoreIngestor() {
        DocumentSplitter splitter = DocumentSplitters.recursive(
                500,
                50,
                new OpenAiTokenizer(GPT_3_5_TURBO));
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel())
                .embeddingStore(pgVectorEmbeddingStore())
                .build();
    }

    public ConversationalRetrievalChain conversationalRetrievalChain() {
        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(OpenAiChatModel.withApiKey("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"))
                .retriever(EmbeddingStoreRetriever.from(pgVectorEmbeddingStore(), embeddingModel()))
                .build();
    }
    public String msg(String msg){
        String question = msg;

        // Embed the question
        Embedding questionEmbedding = this.embeddingModel().embed(question).content();

        // Find relevant embeddings in embedding store by semantic similarity
        // You can play with parameters below to find a sweet spot for your specific use case
        int maxResults = 3;
        double minScore = 0.7;
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings
                = this.pgVectorEmbeddingStore().findRelevant(questionEmbedding, maxResults, minScore);

        PromptTemplate promptTemplate = PromptTemplate.from(
                "You work as an assistant to teacher at School. Generate Quiz for students in  topic :\n"
                        + "{{topic}}\n" +
                        "\n"
                        + "Base your questions on the following information:\n"
                        + "{{information}}");
        String information = relevantEmbeddings.stream()
                .map(match -> match.embedded().text())
                .collect(joining("\n\n"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("topic", question);
        variables.put("information", information);
        Prompt prompt = promptTemplate.apply(variables);
        ChatLanguageModel chatModel = OpenAiChatModel.withApiKey("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");
        AiMessage aiMessage = chatModel.generate(prompt.toUserMessage()).content();

        // See an answer from the model
        String answer = aiMessage.text();
        System.out.println(answer);
        return answer;
    }
    String cc="Class 5";
    String json="\"{\\n    \\\"messaging_product\\\": \\\"whatsapp\\\",\\n    \\\"to\\\": \\\"919543249890\\\",\\n    \\\"type\\\": \\\"template\\\",\\n    \\\"template\\\": {\\n        \\\"name\\\": \\\"tools\\\",\\n        \\\"language\\\": {\\n            \\\"code\\\": \\\"en\\\"\\n        },\\n        \\\"components\\\": [\\n    {\\n        \\\"type\\\": \\\"body\\\",\\n        \\\"parameters\\\": [{\\n            \\\"type\\\": \\\"text\\\",\\n            \\\"text\\\":\\"+cc+"\\\\\n        }]\\n    }]\\n        \\n    }\\n}\"";

    public String loadPDF()
    {
        Document document = loadDocument(toPath("8Social.pdf"),new ApachePdfBoxDocumentParser());
        this.embeddingStoreIngestor().ingest(document);
        return "Loaded";
    }
    private static Path toPath(String fileName) {
        try {
            URL fileUrl = RagApplication.class.getClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
