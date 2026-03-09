---
name: run-generator-with-claude
description: Runs the AI test generator using Claude (Anthropic) instead of OpenAI. Use when the user wants to use Claude, Anthropic, or -p claude for test generation.
---

# Run generator with Claude

## When to use

Use this skill when the user:
- Wants to generate API tests using Claude instead of OpenAI
- Says "use Claude", "run with Anthropic", or "use -p claude"
- Prefers Claude for the spec-to-test generation step

## Workflow

1. **Ensure API key is set.**  
   The user must have `ANTHROPIC_API_KEY` set (e.g. `export ANTHROPIC_API_KEY=your-key`). Do not commit or log the key.

2. **Run the generator with Claude provider.**  
   From the `ai-rest-assured-framework` project root, pass the provider flag (e.g. `-p claude`) along with the spec path:
   ```bash
   mvn exec:java -Dexec.args="--spec <path-to-spec> -p claude"
   ```
   Example:
   ```bash
   mvn exec:java -Dexec.args="--spec src/main/resources/sample-specs/amadeus-flight-most-traveled-destinations.json -p claude"
   ```

3. **Same interactive flow.**  
   The CLI will read the spec, call Claude for test cases, print them for review, and ask for approval before generating REST Assured + TestNG code.

4. **Generated output.**  
   Generated test classes are written under `src/test/java/`, typically in a `generated/` package. Direct the user to open and review those files.

## Notes

- If the CLI uses a different flag for provider (e.g. `--provider claude`), use the project's actual flag. The intent is to run the generator with Anthropic/Claude as the LLM backend.
- OpenAI remains the default when no provider flag is given; this skill is for explicitly using Claude.
