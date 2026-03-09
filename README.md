# AI-Powered REST Assured Framework — Live API Demos

This folder holds **real OpenAPI specs** from [APIs.guru](https://apis.guru) that you can use with the [ai-rest-assured-framework](https://github.com/AniketDev7/ai-rest-assured-framework) so the project is a **live demo**, not a placeholder.

## Why these APIs?

- **Industry specs** — Amadeus (travel) and Google (Hotel Center).
- **Real structure** — Rich schemas, error responses, and parameters for the AI to analyze.
- **Portfolio-ready** — Shows the framework working with production-style specs.

---

## Sample specs (in `sample-specs/`)

| Spec | Source | Base URL | Auth | Description |
|------|--------|----------|------|-------------|
| **amadeus-flight-most-traveled-destinations.json** | [APIs.guru](https://apis.guru/apis/amadeus.com/amadeus-flight-most-traveled-destinations) | `https://test.api.amadeus.com/v1` | OAuth2 (see [Amadeus auth guide](https://developers.amadeus.com/self-service/apis-docs/guides/authorization-262)) | Flight most traveled destinations — air traffic analytics by origin and period. |
| **google-travel-partner-openapi.json** | [APIs.guru](https://apis.guru/apis/google.com) | (per API) | Google OAuth | Travel Partner API — Hotel Center platform (retrieve/update hotel data). |

**Spec URLs (for framework or docs):**

- Amadeus: `https://api.apis.guru/v2/specs/amadeus.com/amadeus-flight-most-traveled-destinations/1.1.1/swagger.json`
- Google: `https://api.apis.guru/v2/specs/google.com/v3/openapi.json`

Both specs are full OpenAPI/Swagger definitions; the AI can use them to generate test cases and REST Assured code. Live calls to these APIs require the respective auth (Amadeus test env, Google Hotel Center credentials).

---

## How to use with the framework

1. **Clone the framework** (if you haven’t):
   ```bash
   git clone https://github.com/AniketDev7/ai-rest-assured-framework.git
   cd ai-rest-assured-framework
   ```

2. **Copy these specs into the framework**  
   Copy the contents of `sample-specs/` into the framework’s spec location, e.g.:
   - `src/main/resources/sample-specs/`

3. **Run the AI test generator** with a real spec:
   ```bash
   # Amadeus Flight Most Traveled Destinations (Swagger 2.0)
   mvn exec:java -Dexec.args="--spec src/main/resources/sample-specs/amadeus-flight-most-traveled-destinations.json"

   # Google Travel Partner API (OpenAPI 3.x)
   mvn exec:java -Dexec.args="--spec src/main/resources/sample-specs/google-travel-partner-openapi.json"
   ```

4. **Base URL / auth for running tests**  
   - **Amadeus:** `https://test.api.amadeus.com/v1` — get an access token from [Amadeus for Developers](https://developers.amadeus.com/) (test env returns a subset of cities).  
   - **Google:** Set the base URL and credentials per [Travel Partner API](https://apis.guru/apis/google.com) / Google Hotel Center docs.

   (e.g. `mvn test -Dapi.base.url=https://test.api.amadeus.com/v1` if your framework supports it; pass token via config or env.)

5. **Cursor and Claude instructions**  
   - **Cursor:** `.cursor/rules/` (API test style, auth, validation, etc.) and `.cursor/skills/` (generate tests from spec, run tests, add auth, fix failing tests, validate spec, refactor base class, logging, run with Claude).  
   - **Claude:** `CLAUDE.md` (project overview, commands) and `.claude/rules/` (api-tests, generator-and-specs, workflows). Use either for consistent test generation and editing.

---

## Quick reference: spec URLs

You can pass these URLs to the framework if it supports loading from URL:

- **Amadeus Flight Most Traveled Destinations:**  
  `https://api.apis.guru/v2/specs/amadeus.com/amadeus-flight-most-traveled-destinations/1.1.1/swagger.json`

- **Google Travel Partner API:**  
  `https://api.apis.guru/v2/specs/google.com/v3/openapi.json`

---
