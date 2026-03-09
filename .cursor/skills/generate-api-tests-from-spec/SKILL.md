---
name: generate-api-tests-from-spec
description: Runs the AI-powered REST Assured test generator with an OpenAPI/Swagger spec path, then directs the user to the generated test files. Use when the user wants to generate API tests from a spec, points at an OpenAPI file, or says "generate tests for this spec."
---

# Generate API tests from OpenAPI spec

## When to use

Use this skill when the user:
- Points at or references an OpenAPI/Swagger spec file and wants to generate tests
- Says "generate tests for this spec" or similar
- Wants to run the AI test generator with a specific spec

## Workflow

1. **Identify the spec file.**  
   Use the file the user pointed at or the path they gave (e.g. `src/main/resources/sample-specs/amadeus-flight-most-traveled-destinations.json` or a path under `sample-specs/`).

2. **Run the generator from the project root.**  
   From the `ai-rest-assured-framework` project root, run:
   ```bash
   mvn exec:java -Dexec.args="--spec <path-to-spec>"
   ```
   Example:
   ```bash
   mvn exec:java -Dexec.args="--spec src/main/resources/sample-specs/amadeus-flight-most-traveled-destinations.json"
   ```
   For Claude instead of OpenAI, add `-p claude` (and set `ANTHROPIC_API_KEY`).

3. **Interactive steps.**  
   The CLI will: read the spec, send it to the LLM for test cases, print them for review, and ask for approval (Y/n). After the user approves, it generates REST Assured + TestNG code.

4. **Generated output.**  
   Generated test classes are written under `src/test/java/` (default), typically in a `generated/` package. Tell the user to open the new file(s) there to review. Example path: `src/test/java/com/qa/api/tests/generated/`.

## Notes

- The user must have `OPENAI_API_KEY` or `ANTHROPIC_API_KEY` set to use the generator.
- Spec path can be relative to the project root or absolute. Prefer paths under `src/main/resources/sample-specs/` when using bundled specs.
