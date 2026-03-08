package com.qa.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.api.ai.LLMClient;
import com.qa.api.ai.LLMClientFactory;
import com.qa.api.ai.LLMProvider;
import com.qa.api.generator.CodeGenerator;
import com.qa.api.generator.SpecAnalyzer;
import com.qa.api.model.GeneratedTestFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Interactive CLI that orchestrates the full workflow:
 *   read spec → AI analysis → test-case review → code generation → file output.
 */
public class AITestGeneratorCLI {

    private static final String BANNER = "\n"
            + "=============================================================\n"
            + "   AI-Powered REST Assured Test Generator\n"
            + "   Supports: OpenAI (GPT-4) | Claude (Anthropic)\n"
            + "=============================================================\n";

    public static void main(String[] args) {
        System.out.println(BANNER);

        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        String specPath = null;
        String provider = "openai";
        String outputDir = "src/test/java";
        String model = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--spec":
                case "-s":
                    specPath = args[++i];
                    break;
                case "--provider":
                case "-p":
                    provider = args[++i];
                    break;
                case "--output":
                case "-o":
                    outputDir = args[++i];
                    break;
                case "--model":
                case "-m":
                    model = args[++i];
                    break;
                case "--help":
                case "-h":
                    printUsage();
                    System.exit(0);
                    break;
                default:
                    if (specPath == null) specPath = args[i];
                    break;
            }
        }

        if (specPath == null) {
            System.err.println("Error: No API specification file provided.\n");
            printUsage();
            System.exit(1);
        }

        try {
            new AITestGeneratorCLI().run(specPath, provider, outputDir, model);
        } catch (Exception e) {
            System.err.println("\nError: " + e.getMessage());
            System.exit(1);
        }
    }

    @SuppressWarnings("unchecked")
    public void run(String specPath, String providerName, String outputDir, String model)
            throws Exception {

        Scanner scanner = new Scanner(System.in);
        SpecAnalyzer specAnalyzer = new SpecAnalyzer();
        CodeGenerator codeGenerator = new CodeGenerator();

        // ── Step 1: Read & parse the OpenAPI spec ──────────────────────
        System.out.println("Step 1: Reading API Specification...");
        String specContent = specAnalyzer.readSpec(specPath);
        Map<String, Object> summary = specAnalyzer.extractSummary(specContent, specPath);

        System.out.println("  File    : " + specPath);
        System.out.println("  API     : " + summary.get("title")
                + " v" + summary.getOrDefault("version", "N/A"));
        if (summary.containsKey("baseUrl")) {
            System.out.println("  Base URL: " + summary.get("baseUrl"));
        }

        List<String> endpoints =
                (List<String>) summary.getOrDefault("endpoints", Collections.emptyList());
        System.out.println("  Endpoints found: " + endpoints.size());
        endpoints.forEach(e -> System.out.println("    • " + e));
        System.out.println();

        // ── Step 2: Send to LLM for test-case generation ──────────────
        LLMProvider llmProvider = LLMProvider.fromString(providerName);
        LLMClient llmClient = (model != null)
                ? LLMClientFactory.create(llmProvider, model)
                : LLMClientFactory.create(llmProvider);

        System.out.println("Step 2: Analyzing with AI (" + llmClient.getProviderName()
                + " / " + llmClient.getModelName() + ")...");
        System.out.println("  Generating test cases — this may take a moment...\n");

        String testCasesJson = specAnalyzer.generateTestCases(llmClient, specContent);

        System.out.println("  AI Analysis Complete!");
        System.out.println("  " + "─".repeat(52));
        System.out.println(formatTestCases(testCasesJson));
        System.out.println();

        // ── Step 3: User approval ─────────────────────────────────────
        System.out.println("Step 3: Review & Approve");
        System.out.print("  Generate REST Assured test code for these test cases? [Y/n]: ");
        String answer = scanner.nextLine().trim();
        if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
            Path saved = Path.of("generated-test-cases.json");
            Files.writeString(saved, testCasesJson);
            System.out.println("  Test-case JSON saved to: " + saved.toAbsolutePath());
            return;
        }
        System.out.println();

        // ── Step 4: Generate REST Assured code ────────────────────────
        System.out.println("Step 4: Generating REST Assured test code...");
        System.out.println("  Using " + llmClient.getProviderName()
                + " / " + llmClient.getModelName() + "...\n");

        String rawCode = codeGenerator.generateCode(llmClient, testCasesJson);
        List<GeneratedTestFile> testFiles = codeGenerator.parseGeneratedCode(rawCode);

        System.out.println("  Generated " + testFiles.size() + " test file(s):");
        testFiles.forEach(f -> System.out.println("    ✓ " + f.getClassName()));
        System.out.println();

        // ── Step 5: Write to disk ─────────────────────────────────────
        System.out.println("Step 5: Save generated tests");
        System.out.print("  Write to " + outputDir + "/com/qa/api/tests/generated/ ? [Y/n]: ");
        answer = scanner.nextLine().trim();
        if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
            System.out.println("\n  Printing generated code to stdout:\n");
            testFiles.forEach(f -> {
                System.out.println("─── " + f.getClassName() + " ───");
                System.out.println(f.getCode());
                System.out.println();
            });
            return;
        }

        codeGenerator.writeTestFiles(testFiles, outputDir);
        System.out.println();
        testFiles.forEach(f ->
                System.out.println("  ✓ Written: " + outputDir
                        + "/com/qa/api/tests/generated/" + f.getClassName()));

        System.out.println("\nDone! Run 'mvn test' to execute the generated tests.");
    }

    // ── helpers ────────────────────────────────────────────────────────

    private String formatTestCases(String testCasesJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(testCasesJson);
            if (!root.isArray()) return "  " + testCasesJson;

            StringBuilder sb = new StringBuilder();
            String currentGroup = "";

            for (JsonNode tc : root) {
                String group = tc.path("method").asText() + " " + tc.path("path").asText();
                if (!group.equals(currentGroup)) {
                    currentGroup = group;
                    sb.append("\n  ").append(group).append('\n');
                }
                sb.append("    [").append(tc.path("category").asText("TEST")).append("] ")
                  .append(tc.path("testName").asText())
                  .append(" — ").append(tc.path("description").asText())
                  .append('\n');
            }
            return sb.toString();
        } catch (Exception e) {
            return "  " + testCasesJson;
        }
    }

    private static void printUsage() {
        System.out.println("Usage:\n"
                + "  mvn exec:java -Dexec.args=\"--spec <spec-file> [options]\"\n\n"
                + "Options:\n"
                + "  --spec,     -s <file>    Path to OpenAPI/Swagger spec (JSON or YAML)\n"
                + "  --provider, -p <name>    LLM provider: 'openai' or 'claude' (default: openai)\n"
                + "  --model,    -m <model>   Override default model name\n"
                + "  --output,   -o <dir>     Test output root (default: src/test/java)\n"
                + "  --help,     -h           Show this help\n\n"
                + "Environment Variables:\n"
                + "  OPENAI_API_KEY           Required when using --provider openai\n"
                + "  ANTHROPIC_API_KEY        Required when using --provider claude\n\n"
                + "Examples:\n"
                + "  # OpenAI (Amadeus Flight Most Traveled Destinations)\n"
                + "  export OPENAI_API_KEY=sk-...\n"
                + "  mvn exec:java -Dexec.args=\"--spec src/main/resources/sample-specs/amadeus-flight-most-traveled-destinations.json\"\n\n"
                + "  # Claude (Google Travel Partner API)\n"
                + "  export ANTHROPIC_API_KEY=sk-ant-...\n"
                + "  mvn exec:java -Dexec.args=\"--spec src/main/resources/sample-specs/google-travel-partner-openapi.json -p claude\"\n");
    }
}
