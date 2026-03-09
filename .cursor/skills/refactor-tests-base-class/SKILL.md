---
name: refactor-tests-base-class
description: Extracts common setup (base URI, auth, RequestSpecification) from multiple test classes into an abstract base class. Use when the user wants to reduce duplication, add a base test class, or share setup across test classes.
---

# Refactor tests to use a base class

## When to use

Use this skill when the user:
- Wants to reduce duplication across API test classes
- Asks to add a base test class or abstract base for tests
- Has several test classes with repeated setup (base URI, auth, content type, logging)

## Workflow

1. **Identify duplication.**  
   Look at the test classes the user pointed at or that live in the same package/module. Note repeated setup: `RestAssured.baseURI`, `given().contentType(...).header("Authorization", ...)`, `.log().ifValidationFails()`, etc.

2. **Design the base class.**  
   - Create an abstract class (e.g. `BaseApiTest`) in the same package or a parent package under `src/test/java`.
   - Add `@BeforeMethod` (or `@BeforeClass` if base URL is static) to build a shared `RequestSpecification`: base URI, content type, auth header, and `.log().ifValidationFails()`. Store it in a field (e.g. `protected RequestSpecification requestSpec`).
   - Read base URI and auth from the same config/env the project uses (system property or config class). Do not hardcode.

3. **Refactor test classes.**  
   - Make each test class extend the base class.
   - Replace duplicated setup with `given(requestSpec).when()...` (or `given().spec(requestSpec).when()...`).
   - Remove per-class `@BeforeMethod` that only set base URI or auth; keep any setup that is specific to that class (e.g. a resource-specific path constant).

4. **Keep tests independent.**  
   Do not put shared mutable state in the base class. Use `@BeforeMethod` for per-test spec if needed (e.g. fresh spec per test). Follow project rules for test isolation.

5. **Verify.**  
   Run the test suite (`mvn test`) to ensure all tests still pass. If the user has TestNG groups, run the same groups as before.

## Notes

- If only one test class exists, a base class may still be useful if the user plans to add more classes. Otherwise, suggest a base class only when at least two classes share the same setup.
- The base class can expose protected helpers (e.g. `getBasePath()`) for subclasses that need different paths.
