---
name: add-auth-to-tests
description: Adds authentication (Bearer token, Basic, or API key) to API tests using config or env and a shared RequestSpecification. Use when the user wants to add auth to tests, use a token, or secure requests.
---

# Add auth to tests

## When to use

Use this skill when the user:
- Wants to add authentication to existing or new API tests
- Says "add Bearer token", "add API key", "use auth", or "secure these tests"
- Needs tests to call an API that requires Authorization or an API key header

## Workflow

1. **Choose auth type.**  
   - **Bearer:** `Authorization: Bearer <token>`. Token from env (e.g. `API_TOKEN`) or config.
   - **Basic:** Username/password from env or config; use `.auth().preemptive().basic(user, pass)`.
   - **API key:** Custom header or query param (e.g. `X-API-Key`) from env or config.

2. **Avoid hardcoding.**  
   Read credentials from `System.getProperty()`, `System.getenv()`, or a small config helper. Document required env vars or properties (e.g. in README). Do not commit real keys.

3. **Attach to requests.**  
   - **Option A – per test:** In each test, add `.header("Authorization", "Bearer " + getToken())` (or equivalent) to `given()`.
   - **Option B – shared spec (preferred):** In `@BeforeMethod` or a base class, build a `RequestSpecification` with the auth header/filter and use `given(requestSpec).when()...` in all tests. Keeps tests DRY and consistent with project rules.

4. **Add 401 test.**  
   Add at least one test that omits or invalidates auth and expects 401 (or 403) to confirm the endpoint is protected.

5. **Optional: auth helper.**  
   If multiple test classes need the same auth, create a small helper (e.g. `TestConfig.getToken()`, `AuthFilter`) in test scope and reuse it in the request spec.

## Notes

- If the project already has a base test class or `RequestSpecification` setup, extend it rather than duplicating.
- For the generator-produced tests, ensure generated code uses the same config/env pattern so it does not hardcode tokens.
