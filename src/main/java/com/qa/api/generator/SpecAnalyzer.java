package com.qa.api.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.qa.api.ai.LLMClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads OpenAPI/Swagger specs, extracts a human-readable summary,
 * and delegates to an LLM for test-case generation.
 */
public class SpecAnalyzer {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public String readSpec(String specPath) throws IOException {
        return Files.readString(Path.of(specPath));
    }

    /**
     * Lightweight parse of an OpenAPI spec to extract title, version,
     * base URL, and a list of endpoint summaries for CLI display.
     */
    public Map<String, Object> extractSummary(String specContent, String specPath) {
        try {
            ObjectMapper mapper = isYaml(specPath) ? yamlMapper : jsonMapper;
            JsonNode root = mapper.readTree(specContent);

            Map<String, Object> summary = new LinkedHashMap<>();

            JsonNode info = root.path("info");
            summary.put("title", info.path("title").asText("Unknown API"));
            summary.put("version", info.path("version").asText("N/A"));
            summary.put("description", info.path("description").asText(""));

            if (root.has("servers")) {
                summary.put("baseUrl", root.path("servers").get(0).path("url").asText(""));
            } else if (root.has("host")) {
                String scheme = root.has("schemes")
                        ? root.path("schemes").get(0).asText("https") : "https";
                String basePath = root.path("basePath").asText("");
                summary.put("baseUrl", scheme + "://" + root.path("host").asText() + basePath);
            }

            List<String> endpoints = new ArrayList<>();
            JsonNode paths = root.path("paths");
            paths.fieldNames().forEachRemaining(path -> {
                JsonNode pathItem = paths.path(path);
                pathItem.fieldNames().forEachRemaining(method -> {
                    if (!method.startsWith("x-") && !method.equals("parameters")
                            && !method.equals("summary") && !method.equals("description")) {
                        String opSummary = pathItem.path(method).path("summary").asText("");
                        endpoints.add(method.toUpperCase() + " " + path
                                + (opSummary.isEmpty() ? "" : " - " + opSummary));
                    }
                });
            });

            summary.put("endpoints", endpoints);
            summary.put("endpointCount", endpoints.size());
            return summary;

        } catch (Exception e) {
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("title", "API Specification");
            fallback.put("endpoints", new ArrayList<>());
            fallback.put("endpointCount", 0);
            fallback.put("note", "Could not parse spec structure — raw content will be sent to AI for analysis");
            return fallback;
        }
    }

    /**
     * Sends the full spec to the LLM and asks it to produce structured test cases (JSON).
     */
    public String generateTestCases(LLMClient llmClient, String specContent) {
        String systemPrompt = loadPrompt("analyze-spec.txt");
        return llmClient.chat(systemPrompt, specContent);
    }

    private boolean isYaml(String path) {
        return path.endsWith(".yaml") || path.endsWith(".yml");
    }

    private String loadPrompt(String fileName) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("prompts/" + fileName)) {
            if (in == null) {
                throw new IOException("Prompt template not found on classpath: prompts/" + fileName);
            }
            return new String(in.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt template: " + fileName, e);
        }
    }
}
