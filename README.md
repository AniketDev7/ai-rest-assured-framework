# AI-Powered REST API Test Framework

A **Java-based API test automation framework** that combines traditional **REST Assured** testing with an **AI-powered test generator**. Feed it an OpenAPI/Swagger spec, and the AI layer (OpenAI GPT-4 or Anthropic Claude) will analyze every endpoint, draft comprehensive test cases, and generate production-ready REST Assured code — all with human review and approval at each step.

## Why This Exists

Writing API tests is repetitive. Given an API specification, most of the test logic — positive/negative paths, boundary checks, status-code assertions — can be inferred automatically. This framework lets QA engineers:

1. **Feed** an OpenAPI/Swagger spec (JSON or YAML)
2. **Review** AI-generated test cases (positive, negative, boundary, error)
3. **Approve** and generate REST Assured + TestNG code automatically
4. **Run** the generated tests with `mvn test`

## Tech Stack

| Component | Details |
|-----------|---------|
| Language | Java 11+ |
| API Testing | [REST Assured](https://rest-assured.io/) 5.3.2 |
| Test Runner | TestNG 7.8.0 |
| Build | Maven |
| AI Providers | OpenAI (GPT-4) / Anthropic Claude |
| Spec Parsing | Jackson + jackson-dataformat-yaml |

## Project Structure

```
ai-rest-assured-framework/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/qa/api/
│   │   │   ├── AITestGeneratorCLI.java        # CLI entry point
│   │   │   ├── ai/
│   │   │   │   ├── LLMClient.java             # Provider interface
│   │   │   │   ├── LLMProvider.java            # Enum (OPENAI, CLAUDE)
│   │   │   │   ├── LLMClientFactory.java       # Factory
│   │   │   │   ├── OpenAIClient.java           # OpenAI GPT integration
│   │   │   │   └── ClaudeClient.java           # Anthropic Claude integration
│   │   │   ├── generator/
│   │   │   │   ├── SpecAnalyzer.java           # Reads & sends spec to LLM
│   │   │   │   └── CodeGenerator.java          # Generates REST Assured code
│   │   │   └── model/
│   │   │       ├── TestCase.java               # Test case data model
│   │   │       └── GeneratedTestFile.java      # Generated file wrapper
│   │   └── resources/
│   │       ├── prompts/
│   │       │   ├── analyze-spec.txt            # Prompt for test-case generation
│   │       │   └── generate-code.txt           # Prompt for code generation
│   │       └── sample-specs/
│   │           └── petstore-openapi.json        # Demo spec (Petstore API)
│   └── test/
│       ├── java/com/qa/api/tests/
│       │   ├── SampleApiTest.java              # Hand-written sample tests
│       │   └── generated/                      # AI-generated tests land here
│       └── resources/
│           └── testng.xml
```

## Quick Start

### Prerequisites

- JDK 11+
- Maven 3.6+
- An API key for **OpenAI** or **Anthropic Claude**

### 1. Clone & Build

```bash
git clone <repo-url>
cd ai-rest-assured-framework
mvn clean compile
```

### 2. Set Your API Key

```bash
# For OpenAI
export OPENAI_API_KEY=sk-...

# For Anthropic Claude
export ANTHROPIC_API_KEY=sk-ant-...
```

### 3. Run the AI Test Generator

```bash
# Using the bundled Petstore spec with OpenAI
mvn exec:java -Dexec.args="--spec src/main/resources/sample-specs/petstore-openapi.json"

# Using Claude instead
mvn exec:java -Dexec.args="--spec my-api-spec.yaml --provider claude"

# Custom model
mvn exec:java -Dexec.args="--spec spec.json --provider openai --model gpt-4-turbo"
```

### 4. Run the Generated Tests

```bash
mvn test
```

## How It Works

```
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│  OpenAPI/Swagger │     │  LLM (GPT-4 or   │     │   REST Assured   │     │  Save to Disk    │
│  Specification   │────>│     Claude)      │────>│     Test Code    │────>│  (.java files)   │
│  (.json / .yaml) │     │                  │     │    Generation    │     │                  │
└──────────────────┘     └──────────────────┘     └──────────────────┘     └──────────────────┘
         │                        │                        │                        │
    Step 1: Parse         Step 2: AI analyzes       Step 4: Generate          Step 5: Write
     & summarize         & produces test cases       Java test code             to project
                                  │
                         Step 3: Human reviews
                               & approves
```

**Step 1** — The CLI reads your OpenAPI spec and extracts a summary (endpoints, methods, schemas).

**Step 2** — The full spec is sent to the chosen LLM with a carefully crafted prompt. The AI returns structured test cases covering positive, negative, boundary, and error scenarios.

**Step 3** — You review the generated test cases in the terminal and approve code generation.

**Step 4** — The approved test cases are sent back to the LLM with a code-generation prompt. The AI produces complete REST Assured + TestNG Java test classes.

**Step 5** — Generated files are written to `src/test/java/com/qa/api/tests/generated/`.

## CLI Options

| Flag | Description | Default |
|------|-------------|---------|
| `--spec`, `-s` | Path to OpenAPI/Swagger spec file | *(required)* |
| `--provider`, `-p` | LLM provider: `openai` or `claude` | `openai` |
| `--model`, `-m` | Override the default model | `gpt-4` / `claude-sonnet-4-20250514` |
| `--output`, `-o` | Output root directory for generated tests | `src/test/java` |

## Example Output

Running against the included Petstore spec:

```
=============================================================
   AI-Powered REST Assured Test Generator
   Supports: OpenAI (GPT-4) | Claude (Anthropic)
=============================================================

Step 1: Reading API Specification...
  File    : petstore-openapi.json
  API     : Petstore API v1.0.0
  Base URL: https://petstore.swagger.io/v2
  Endpoints found: 7
    • GET /pets - List all pets
    • POST /pets - Create a pet
    • GET /pets/{petId} - Get a pet by ID
    • PUT /pets/{petId} - Update a pet
    • DELETE /pets/{petId} - Delete a pet
    • GET /store/inventory - Returns pet inventories by status
    • POST /store/orders - Place an order for a pet

Step 2: Analyzing with AI (OpenAI / gpt-4)...
  AI Analysis Complete!
  ────────────────────────────────────────────────────────────

  GET /pets
    [POSITIVE] shouldReturnAllPets — List pets returns 200
    [NEGATIVE] shouldReturn400_whenLimitIsNegative — Invalid limit
    [BOUNDARY] shouldReturnEmptyList_whenNoMatchingStatus — Empty result
    ...

Step 3: Review & Approve
  Generate REST Assured test code for these test cases? [Y/n]: Y

Step 4: Generating REST Assured test code...
  Generated 2 test file(s):
    ✓ PetApiTest.java
    ✓ StoreApiTest.java

Step 5: Save generated tests
  ✓ Written: src/test/java/com/qa/api/tests/generated/PetApiTest.java
  ✓ Written: src/test/java/com/qa/api/tests/generated/StoreApiTest.java

Done! Run 'mvn test' to execute the generated tests.
```

## Running Hand-Written Tests

The framework also includes hand-written sample tests:

```bash
mvn test
# Or with the TestNG suite directly:
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

## Configuration

- **API Keys**: Set via environment variables (`OPENAI_API_KEY` / `ANTHROPIC_API_KEY`)
- **Base URL for tests**: Generated tests read from `api.base.url` system property:
  ```bash
  mvn test -Dapi.base.url=https://your-api.example.com
  ```

## Architecture Highlights

- **Provider-agnostic LLM layer** — swap between OpenAI and Claude with a single flag
- **Two-phase generation** — AI produces test *cases* first (for review), then *code* (after approval)
- **Prompt engineering** — carefully crafted prompts ensure structured, parseable output
- **No heavy dependencies** — uses Java 11's built-in `HttpClient` for LLM API calls
- **Human-in-the-loop** — nothing is generated or saved without explicit user approval

## License

MIT
