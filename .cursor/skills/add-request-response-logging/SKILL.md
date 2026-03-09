---
name: add-request-response-logging
description: Adds request and response logging to API tests using Rest Assured's log() (e.g. ifValidationFails) without exposing secrets. Use when the user wants to add logging, debug requests, or log on failure.
---

# Add request/response logging

## When to use

Use this skill when the user:
- Wants to add request or response logging to API tests
- Asks to log on failure, debug requests, or see what was sent/received
- Needs to troubleshoot failing tests by inspecting request/response

## Workflow

1. **Prefer log on failure.**  
   Add `.log().ifValidationFails()` to the request spec (or to `given()`) so request and response are logged only when a test fails. Keeps CI output clean.

2. **Optional: log body only.**  
   Use `.log().ifValidationFails(LogDetail.BODY)` to limit logged detail. Use `.log().body()` only for local debugging; remove or guard with a flag before committing if noisy.

3. **Never log secrets.**  
   Do not log headers or body that contain Authorization, API keys, or passwords. If using custom logging or filters, strip or redact sensitive fields. Follow project rule: never log secrets.

4. **Where to add.**  
   - On the shared `RequestSpecification` in `@BeforeMethod` or base class so all tests get it, or
   - Per test: `given().log().ifValidationFails().when()...`

5. **Verify.**  
   Run a test that fails on purpose (e.g. wrong status code) and confirm logs show request/response. Confirm auth header or body is not printed in plain text.

## Notes

- Project rule: use `log().ifValidationFails()`; avoid `log().all()` on every request in CI. Use TestNG and assertions for outcomes, not System.out for important checks.
