package com.qa.api.ai;

public enum LLMProvider {

    OPENAI("openai", "OPENAI_API_KEY"),
    CLAUDE("claude", "ANTHROPIC_API_KEY");

    private final String id;
    private final String envKeyName;

    LLMProvider(String id, String envKeyName) {
        this.id = id;
        this.envKeyName = envKeyName;
    }

    public String getId() {
        return id;
    }

    public String getEnvKeyName() {
        return envKeyName;
    }

    public static LLMProvider fromString(String value) {
        for (LLMProvider p : values()) {
            if (p.id.equalsIgnoreCase(value)) {
                return p;
            }
        }
        throw new IllegalArgumentException(
                "Unknown LLM provider: '" + value + "'. Supported providers: 'openai', 'claude'.");
    }
}
