---
name: run-api-tests
description: Runs the project's API tests with Maven. Use when the user wants to run tests, run the test suite, run smoke tests, or verify tests after changes.
---

# Run API tests

## When to use

Use this skill when the user:
- Says "run the tests", "run API tests", "run test suite", or "verify tests"
- Wants to run tests after generating or editing test code
- Asks to run only smoke or a specific group of tests

## Workflow

1. **Default: run all tests.**  
   From the `ai-rest-assured-framework` project root:
   ```bash
   mvn test
   ```

2. **Run by TestNG group.**  
   If the project uses groups (e.g. `smoke`, `regression`):
   ```bash
   mvn test -Dgroups=smoke
   ```
   Or multiple: `-Dgroups="smoke,regression"`.

3. **Run with a specific testng.xml suite.**  
   If a suite file exists (e.g. `src/test/resources/testng.xml` or a custom one):
   ```bash
   mvn test -DsuiteXmlFile=src/test/resources/testng.xml
   ```

4. **Run a single test class.**  
   ```bash
   mvn test -Dtest=MyApiTest
   ```

5. **Report results.**  
   Surefire writes reports to `target/surefire-reports/`. Tell the user the outcome (passed/failed) and, if failed, point to the report or the failing test name and assertion.

## Notes

- Ensure `api.base.url` or required env vars are set if tests depend on them; otherwise tests may fail or be skipped.
- For CI, use the same command (e.g. `mvn test`) so the build is reproducible.
