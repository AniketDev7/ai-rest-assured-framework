# API test standards (REST Assured + TestNG)

When writing or generating API tests:

- **Structure:** Use `given().when().then()` (REST Assured). Use TestNG (`@Test`, assertions).
- **Assertions:** Assert status code explicitly with `.statusCode(...)`. Prefer hard assertions; avoid soft assertions unless requested.
- **Response body:** Assert body where relevant (e.g. `.body("field", equalTo(value))`). Use Hamcrest matchers from `org.hamcrest.Matchers`.
- **Negative cases:** For every happy-path test, include at least one negative case (invalid ID → 404, missing field → 400, unauthenticated → 401) where the API defines it.
- **Base URL / auth:** Use configurable base URL (e.g. `api.base.url` system property) and auth from env/config. Never hardcode URLs or tokens.
- **Naming:** `shouldReturn{Status}_when{Condition}` (e.g. `shouldReturn200_whenUserExists`, `shouldReturn404_whenUserNotFound`).
- **Isolation:** Each test independent; no shared mutable state. Use `@BeforeMethod` for setup; optional `@AfterMethod` for cleanup.
- **Logging:** Use `.log().ifValidationFails()`; never log Authorization or API keys.
