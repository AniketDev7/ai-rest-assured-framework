---
name: add-negative-test-cases
description: Adds negative API test cases (400, 404, 401, etc.) to an existing test class following project rules. Use when the user wants to add negative tests, cover error cases, or add 404/400 tests.
---

# Add negative test cases

## When to use

Use this skill when the user:
- Asks to add negative tests, error cases, or failure scenarios to an API test class
- Says "add 404 tests", "add validation tests", or "cover 400/401"
- Wants to align a test class with the project rule that every happy-path test should have at least one negative case

## Workflow

1. **Locate the test class.**  
   Use the file the user pointed at or the test class they named (e.g. `*Test.java`, `*ApiTest.java`).

2. **Review existing tests.**  
   Identify happy-path tests (e.g. 200, 201) that do not yet have a matching negative case.

3. **Add negative cases per project rules.**  
   - **404:** Invalid or non-existent ID (e.g. `shouldReturn404_whenResourceNotFound`). Use a clearly invalid ID or one that does not exist in the test environment.
   - **400:** Missing required field, wrong type, or invalid value (e.g. `shouldReturn400_whenRequiredFieldMissing`). Send minimal body that triggers validation.
   - **401:** Omit or invalidate auth when the endpoint is protected (e.g. `shouldReturn401_whenUnauthenticated`).
   - **403:** Valid auth but insufficient permissions when the API distinguishes 403 (e.g. `shouldReturn403_whenForbidden`).

4. **Follow project conventions.**  
   - Use `given().when().then()` and `.statusCode(...)`.
   - Name methods: `shouldReturn{Status}_when{Condition}`.
   - Assert error response body when the API returns a structured error (e.g. `.body("error", notNullValue())`).
   - Reuse the same base URI and auth setup as other tests in the class (e.g. shared `RequestSpecification`).

5. **Keep tests independent.**  
   Do not rely on execution order. Use valid but "negative" inputs (e.g. wrong ID) rather than depending on another test to have run first.

## Notes

- Add only the negative cases that the OpenAPI spec or API contract defines (e.g. 404 for "not found", 400 for "validation error").
- One negative test per scenario; avoid multiple status codes in one test method.
