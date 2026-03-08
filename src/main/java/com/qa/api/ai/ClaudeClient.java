package com.qa.api.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ClaudeClient implements LLMClient {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String DEFAULT_MODEL = "claude-sonnet-4-20250514";
    private static final String API_VERSION = "2023-06-01";

    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public ClaudeClient(String apiKey) {
        this(apiKey, DEFAULT_MODEL);
    }

    public ClaudeClient(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.mapper = new ObjectMapper();
    }

    @Override
    public String chat(String systemPrompt, String userMessage) {
        try {
            ObjectNode body = mapper.createObjectNode();
            body.put("model", model);
            body.put("max_tokens", 4096);
            body.put("system", systemPrompt);

            ArrayNode messages = body.putArray("messages");
            ObjectNode usrMsg = messages.addObject();
            usrMsg.put("role", "user");
            usrMsg.put("content", userMessage);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", API_VERSION)
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .timeout(Duration.ofMinutes(3))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Claude API error (HTTP " + response.statusCode() + "): " + response.body());
            }

            JsonNode json = mapper.readTree(response.body());
            return json.path("content").get(0).path("text").asText();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Claude API: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "Claude (Anthropic)";
    }

    @Override
    public String getModelName() {
        return model;
    }
}
