package com.qa.api.ai;

public class LLMClientFactory {

    public static LLMClient create(LLMProvider provider) {
        String apiKey = resolveApiKey(provider);
        switch (provider) {
            case OPENAI:
                return new OpenAIClient(apiKey);
            case CLAUDE:
                return new ClaudeClient(apiKey);
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    public static LLMClient create(LLMProvider provider, String model) {
        String apiKey = resolveApiKey(provider);
        switch (provider) {
            case OPENAI:
                return new OpenAIClient(apiKey, model);
            case CLAUDE:
                return new ClaudeClient(apiKey, model);
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    private static String resolveApiKey(LLMProvider provider) {
        String apiKey = System.getenv(provider.getEnvKeyName());
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "API key not found. Set the " + provider.getEnvKeyName() + " environment variable.\n"
                            + "  Example: export " + provider.getEnvKeyName() + "=your-key-here");
        }
        return apiKey;
    }
}
