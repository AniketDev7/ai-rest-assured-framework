# Generator and OpenAPI specs

## Running the generator

- From project root: `mvn exec:java -Dexec.args="--spec <path-to-spec>"`.
- Use `-p claude` for Claude (set `ANTHROPIC_API_KEY`); default is OpenAI (`OPENAI_API_KEY`).
- Spec path: relative to project root or absolute. Prefer `src/main/resources/sample-specs/` or `sample-specs/`.
- CLI is interactive: prints proposed test cases, asks for approval, then writes Java under `src/test/java/` (e.g. `.../generated/`).

## OpenAPI spec conventions

- Valid OpenAPI 3.x or Swagger 2.0 (JSON or YAML). Root: `openapi`/`swagger`, `info`, `paths`.
- Each path has at least one operation with `responses` (e.g. 200, 404). Document 4xx where applicable so the generator produces negative tests.
- No secrets in spec files; use securitySchemes with placeholder names; real keys from env/config.
- Specs live in `sample-specs/` or `src/main/resources/sample-specs/`.

## Main Java (CLI / generator)

- CLI: `AITestGeneratorCLI`; args `--spec`, `--provider`/`-p`, `--output-dir`, `--model`. Print usage on `--help`.
- Keep spec parsing, prompt loading, and file writing separate. Use existing `CodeGenerator` and prompts under `src/main/resources/prompts/`.
