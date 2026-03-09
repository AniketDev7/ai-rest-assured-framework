# Workflows (when to do what)

- **Generate tests from spec:** User points at OpenAPI file or says "generate tests for this spec" → run `mvn exec:java -Dexec.args="--spec <path>"` (add `-p claude` for Claude). Then point them to generated files under `src/test/java/.../generated/`.

- **Run tests:** User says "run tests" or "run smoke" → `mvn test` or `mvn test -Dgroups=smoke`. On failure, point to `target/surefire-reports/`.

- **Add negative tests:** User wants 404/400/401 coverage → add test methods following project naming and style; one negative scenario per test; reuse existing spec/auth setup.

- **Add auth to tests:** User wants Bearer/API key → read token from env/config; set on shared `RequestSpecification` or in `@BeforeMethod`; add one test that omits/invalidates auth and expects 401.

- **Fix failing test:** User reports failure → identify status vs assertion vs env (base URL, auth, missing env var). Fix test or config; do not remove assertions or hardcode secrets.

- **Validate spec:** User wants to check OpenAPI file → verify structure (openapi, info, paths), operations with responses; suggest adding 4xx and examples for better generation.

- **Refactor to base class:** Multiple test classes with duplicated setup → extract abstract base with `@BeforeMethod` building shared `RequestSpecification`; have test classes extend it and use `given(requestSpec).when()...`.
