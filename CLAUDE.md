# AI-Powered REST Assured Framework

Java 11, Maven. REST Assured 5.x + TestNG for API tests. AI generator reads OpenAPI/Swagger specs and produces REST Assured + TestNG code (LLM: OpenAI or Claude).

## Commands

- **Generate tests from spec:**  
  `mvn exec:java -Dexec.args="--spec <path>"`  
  With Claude: add `-p claude` (requires `ANTHROPIC_API_KEY`).  
  Default: OpenAI (`OPENAI_API_KEY`).

- **Run tests:**  
  `mvn test`  
  Optional: `-Dgroups=smoke`, `-Dtest=MyApiTest`, `-DsuiteXmlFile=src/test/resources/testng.xml`.

- **Single test class:**  
  `mvn test -Dtest=MyApiTest`

## Layout

- `src/main/java/com/qa/api/` — CLI (`AITestGeneratorCLI`), generator, LLM clients (OpenAI, Claude), prompts.
- `src/test/java/` — API test classes; generated code often under `.../generated/`.
- `src/main/resources/sample-specs/`, `sample-specs/` — OpenAPI/Swagger JSON or YAML.
- `src/main/resources/prompts/` — LLM prompt templates (analyze-spec, generate-code).

## Conventions

- API tests: `given().when().then()`, explicit `.statusCode(...)`, TestNG `@Test`. No hardcoded base URL or secrets; use config/env.
- For each happy-path test, add at least one negative case (404, 400, 401) where the API defines it.
- Test names: `shouldReturn{Status}_when{Condition}`. One status per test.
- More detail: `.claude/rules/` (and Cursor users: `.cursor/rules/`, `.cursor/skills/`).
