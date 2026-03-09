---
name: validate-openapi-spec
description: Validates an OpenAPI/Swagger spec file for structure, required fields, and usability by the test generator. Use when the user wants to validate a spec, check if a spec is valid, or improve a spec for test generation.
---

# Validate OpenAPI spec

## When to use

Use this skill when the user:
- Asks to validate an OpenAPI or Swagger spec file
- Wants to check if a spec is valid or ready for the test generator
- Asks to improve or fix a spec for better test generation

## Workflow

1. **Identify the spec file.**  
   Use the file the user pointed at or the path they gave (under `sample-specs/`, `src/main/resources/sample-specs/`, or elsewhere).

2. **Check structure.**  
   - **OpenAPI 3.x:** Root must have `openapi` (e.g. `3.0.x`), `info`, and `paths`. Optionally `servers`, `components/schemas`, `security`.
   - **Swagger 2.0:** Root must have `swagger: "2.0"`, `info`, and `paths`.
   - **Format:** Valid JSON or YAML (no trailing commas, valid quoting). If YAML, ensure the project has a YAML parser (e.g. Jackson) and the file is loadable.

3. **Check paths and operations.**  
   - Each path should have at least one operation (get, post, put, delete, patch).
   - Operations should have `responses` with at least one status code (e.g. 200, 201). The generator uses these to propose test cases.
   - If the API requires auth, document `security` at operation or global level so generated tests can include auth.

4. **Suggest improvements for test generation.**  
   - Add response schemas or examples so generated assertions are more precise.
   - Document 4xx responses (400, 404, 401) where applicable so the generator produces negative tests.
   - Ensure `servers` or a base URL is documented if tests need a default base.

5. **Report.**  
   Summarize: valid or list of issues (e.g. missing `paths`, invalid JSON, missing responses). If valid, note any optional improvements. Do not include secrets; if the spec contains placeholders for keys, that is fine.

## Notes

- The project's generator may support only a subset of OpenAPI (e.g. paths, methods, response codes). Mention any generator-specific constraints if known.
- For quick validation, running the generator with the spec (`mvn exec:java -Dexec.args="--spec <path>"`) and checking for parse errors also validates that the spec is loadable.
