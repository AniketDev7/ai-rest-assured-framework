package com.qa.api.ai;

/**
 * Abstraction over LLM providers (OpenAI, Anthropic Claude).
 * Each implementation handles its own API protocol.
 */
public interface LLMClient {

    String chat(String systemPrompt, String userMessage);

    String getProviderName();

    String getModelName();
}
