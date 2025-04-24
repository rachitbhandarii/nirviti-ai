package com.nirviti.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class AiService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    public AiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String processContent(AiRequest request) {
        // Build the prompt
        String prompt = buildPrompt(request);

        // Query the AL Model API=
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text",prompt)
                        })
                }
        );
        String response = webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Parse the response
        return extractTextFromResponse(response);
    }

    private String extractTextFromResponse(String response) {
        String text = "";
        try {
            GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);
            if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
                if (firstCandidate.getContent() != null && firstCandidate.getContent().getParts() != null && !firstCandidate.getContent().getParts().isEmpty()) {
                    text = firstCandidate.getContent().getParts().get(0).getText();
                }
            }
        } catch (Exception e) {
            text = "Error Parsing: " + e.getMessage();
        }
        return text;
    }

    private String buildPrompt(AiRequest request) {
        StringBuilder prompt = new StringBuilder();

        switch (request.getOperation()) {
            case "summarize prescription":
                prompt.append("Assume you are a doctor. At the end of the paragraph always write - consult doctor for specific analysis. Provide a clear and concise information in one paragraph without bullet points on what the diseases could be given the prescription as follows:\n\n");
                break;
            case "summarize items":
                prompt.append("Provide a clear and concise information in one paragraph without bullet points on how the sales have been and in what specific field of medecines is this pharmacy lacking in:\n\n");
                break;
            case "summarize season":
                prompt.append("Given today's month give me a seasonal alert of any health condition that may surge in India, in just one small line.\n\n");
                break;
            case "patient load":
                prompt.append("Given today how much on an average can the forcasted patient load increase or decrease in New Delhi, India, give me an answer like this in one line only. If you dont know give a random guess. {increase by 20% this weekend}\n\n");
                break;
            case "blood shortage":
                prompt.append("Given the data on blood groups tell which blood group has blood shortage. give answer like this (B+ units running low)\n\n");
                break;
            case "suggest":
                prompt.append("Based on the following content suggest related topics and further reading. Format the response with clear headings and bullet points:\n\n");
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + request.getOperation());
        }
        prompt.append(request.getContent());

        return prompt.toString();
    }
}
