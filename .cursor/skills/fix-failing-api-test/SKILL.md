---
name: fix-failing-api-test
description: Debugs and fixes a failing API test by inspecting assertions, status code, response body, and environment. Use when a test fails, the user pastes a failure message, or they ask why a test is failing.
---

# Fix failing API test

## When to use

Use this skill when the user:
- Reports that an API test is failing or pastes a failure output
- Asks "why is this test failing?" or "fix this test"
- Shares a Surefire report snippet or assertion error for a test in this project

## Workflow

1. **Get failure details.**  
   Use the test name and stack trace or assertion message the user provided. If needed, run the failing test locally: `mvn test -Dtest=TestClassName#methodName` and capture the output.

2. **Identify failure type.**  
   - **Status code mismatch:** Expected 200, got 401/404/500. Check: base URL, auth (token/header), path/query params, and whether the API or test data changed.
   - **Response body assertion:** e.g. `Expected: "x", Actual: "y"`. Check: JsonPath expression, expected value, and whether the API response shape or data changed.
   - **Connection/timeout:** Connection refused, timeout. Check: base URL, network, and whether the service is running or reachable.
   - **Environment:** Missing env var or property (e.g. `api.base.url`, `OPENAI_API_KEY`). Remind user to set it or add a clear skip message when unset.

3. **Fix the test or environment.**  
   - **If the test is wrong:** Update expected status or assertion to match the API contract. Add or correct auth, path, or body per project rules.
   - **If the API or data changed:** Update test data (e.g. IDs, payloads) or expected values. Prefer stable test data or config so tests are resilient.
   - **If the environment is wrong:** Document or add a check for required config; fail fast with a clear message when base URL or auth is missing.

4. **Avoid masking real failures.**  
   Do not remove assertions or broaden matchers (e.g. from `equalTo(x)` to `notNullValue()`) unless the contract truly allows it. Prefer fixing the request or expected value.

5. **Re-run.**  
   Suggest running the test again (`mvn test -Dtest=...`) and confirm the fix. If flaky, suggest adding timeout or retry only when justified by the API behavior.

## Notes

- Follow project rules: explicit status code, `given().when().then()`, no hardcoded secrets. Use config for base URL and auth.
- If the failure is in generated code, fix it and optionally suggest a prompt or generator change so future generated tests do not repeat the issue.
