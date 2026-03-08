package com.qa.api.generator;

import com.qa.api.ai.LLMClient;
import com.qa.api.model.GeneratedTestFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes structured test-case JSON from SpecAnalyzer, sends it to the LLM,
 * and parses the returned REST Assured Java code into discrete files.
 */
public class CodeGenerator {

    private static final Pattern FILE_BLOCK = Pattern.compile(
            "---FILE:\\s*(\\w+\\.java)---\\s*\\n(.*?)---END FILE---",
            Pattern.DOTALL);

    private static final Pattern CLASS_NAME = Pattern.compile(
            "public\\s+class\\s+(\\w+)");

    /**
     * Asks the LLM to produce REST Assured test code for the given test-case JSON.
     */
    public String generateCode(LLMClient llmClient, String testCasesJson) {
        String systemPrompt = loadPrompt("generate-code.txt");
        return llmClient.chat(systemPrompt, testCasesJson);
    }

    /**
     * Parses the LLM response into individual GeneratedTestFile objects.
     * Handles the ---FILE / ---END FILE--- markers, and falls back to
     * treating the entire response as a single file when markers are absent.
     */
    public List<GeneratedTestFile> parseGeneratedCode(String llmResponse) {
        List<GeneratedTestFile> files = new ArrayList<>();
        Matcher m = FILE_BLOCK.matcher(llmResponse);

        while (m.find()) {
            files.add(new GeneratedTestFile(m.group(1), m.group(2).trim()));
        }

        if (files.isEmpty()) {
            String code = stripMarkdownFences(llmResponse);
            if (!code.isBlank()) {
                Matcher classMatch = CLASS_NAME.matcher(code);
                String name = classMatch.find()
                        ? classMatch.group(1) + ".java"
                        : "GeneratedApiTest.java";
                files.add(new GeneratedTestFile(name, code));
            }
        }

        return files;
    }

    /**
     * Writes the generated Java files into the output directory,
     * placing them under com/qa/api/tests/generated/.
     */
    public void writeTestFiles(List<GeneratedTestFile> files, String outputDir) throws IOException {
        Path base = Path.of(outputDir, "com", "qa", "api", "tests", "generated");
        Files.createDirectories(base);

        for (GeneratedTestFile file : files) {
            Files.writeString(base.resolve(file.getClassName()), file.getCode());
        }
    }

    private String stripMarkdownFences(String text) {
        return text.replaceAll("```java\\s*\\n?", "")
                   .replaceAll("```\\s*\\n?", "")
                   .trim();
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
