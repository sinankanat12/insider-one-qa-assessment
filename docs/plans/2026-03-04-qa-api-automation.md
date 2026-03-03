# QA API Automation — Petstore Pet Endpoints Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a production-ready API test suite in Java/Maven/RestAssured that covers CRUD operations on the Petstore `/pet` endpoints with positive and negative scenarios, Allure reporting, Docker containerisation, and GitHub Actions CI.

**Architecture:** Tests are organised using the API Client pattern (adapted POM for APIs): `BaseClient` owns RestAssured configuration, `PetClient` owns all `/pet` endpoint calls, and test classes contain only assertion logic. `PetFactory` produces isolated test data objects with unique IDs so no test shares state with another.

**Tech Stack:** Java 17, Maven 3.9, RestAssured 5.4.0, JUnit 5.10.2, Jackson 2.17.0, Allure 2.25.0, Docker, GitHub Actions

---

## Background: Why These Decisions

| Decision | Reason |
|---|---|
| RestAssured over OkHttp/Unirest | Industry standard for Java API testing; fluent DSL; built-in JSON/XML validation; Allure adapter exists |
| JUnit 5 over TestNG | Better extension model; `@BeforeEach` lifecycle is cleaner for test isolation; native Allure adapter |
| API Client pattern (not true Page Objects) | POM was designed for UI; for APIs the equivalent is a "Client" class per resource — same goal: keep HTTP details out of tests |
| PetFactory as static factory methods | Avoids builder complexity for a simple POJO; each method returns a ready-to-use `Pet` with a unique ID (timestamp-based) |
| No Cucumber/BDD | Adds indirection without value for API tests; JUnit5 + clear method names are self-documenting enough |
| `long id = System.currentTimeMillis()` for IDs | Ensures uniqueness across parallel runs without a DB sequence; cheap and deterministic in logs |
| Docker multi-stage build | Keeps final image small; CI doesn't need a local JDK |
| GitHub Pages for Allure report | Free, zero-config hosting; artifacts also uploaded as fallback |

---

## Project Location

All work happens inside: `api-automation/`
(relative to repo root `/Users/sinankanat/Desktop/insider-one-qa-assessment/`)

---

## Final Directory Structure

```
insider-one-qa-assessment/          ← repo root
├── .github/
│   └── workflows/
│       └── api-automation.yml      ← GitHub Actions reads ONLY from here
├── api-automation/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   │       └── com/insiderone/qa/
│   │   │           ├── client/
│   │   │           │   ├── BaseClient.java
│   │   │           │   └── PetClient.java
│   │   │           ├── model/
│   │   │           │   ├── Pet.java
│   │   │           │   ├── Category.java
│   │   │           │   └── Tag.java
│   │   │           └── factory/
│   │   │               └── PetFactory.java
│   │   └── test/
│   │       ├── java/
│   │       │   └── com/insiderone/qa/tests/pet/
│   │       │       ├── CreatePetTest.java
│   │       │       ├── GetPetTest.java
│   │       │       ├── UpdatePetTest.java
│   │       │       ├── DeletePetTest.java
│   │       │       └── FindByStatusTest.java
│   │       └── resources/
│   │           └── allure.properties
│   ├── Dockerfile
│   ├── pom.xml
│   └── README.md
└── ...
```

> **Why root `.github/workflows/`?** GitHub Actions discovers workflow files exclusively from the repository root `.github/workflows/` directory. A `workflows/` folder placed inside a subdirectory (e.g. `api-automation/.github/`) is silently ignored — GitHub never reads it.

---

## Test Scenarios

### Positive Scenarios

| # | Test Class | Method Name | Endpoint | Description |
|---|---|---|---|---|
| P1 | `CreatePetTest` | `createPetWithAllFields_returns200` | `POST /pet` | Full payload, verify HTTP 200 and returned ID matches |
| P2 | `CreatePetTest` | `createPetWithMinimalFields_returns200` | `POST /pet` | Only name + status, no category/tags |
| P3 | `GetPetTest` | `getPetByValidId_returns200WithCorrectData` | `GET /pet/{petId}` | Create then GET, verify all fields |
| P4 | `UpdatePetTest` | `updatePetName_returns200AndNamePersists` | `PUT /pet` | PUT with changed name, verify via GET |
| P5 | `UpdatePetTest` | `updatePetStatus_returns200AndStatusPersists` | `PUT /pet` | PUT with changed status, verify via GET |
| P6 | `DeletePetTest` | `deleteExistingPet_returns200` | `DELETE /pet/{petId}` | Create then DELETE, verify HTTP 200 |
| P7 | `DeletePetTest` | `deletedPet_isNoLongerRetrievable` | `GET /pet/{petId}` after DELETE | GET after DELETE returns 404 |
| P8 | `FindByStatusTest` | `findByStatusAvailable_returns200WithNonEmptyList` | `GET /pet/findByStatus?status=available` | List not empty, all items have status "available" |
| P9 | `FindByStatusTest` | `findByStatusPending_returns200` | `GET /pet/findByStatus?status=pending` | HTTP 200, response is array |
| P10 | `FindByStatusTest` | `findByStatusSold_returns200` | `GET /pet/findByStatus?status=sold` | HTTP 200, response is array |

### Negative Scenarios

| # | Test Class | Method Name | Endpoint | Description | Expected HTTP |
|---|---|---|---|---|---|
| N1 | `GetPetTest` | `getPetWithNonExistentId_returns404` | `GET /pet/{petId}` | ID = `Long.MAX_VALUE` | 404 |
| N2 | `DeletePetTest` | `deleteNonExistentPet_returns404` | `DELETE /pet/{petId}` | ID that was never created | 404 |
| N3 | `FindByStatusTest` | `findByInvalidStatus_returns400` | `GET /pet/findByStatus?status=invalid_xyz` | Non-enum status value | 400 |
| N4 | `GetPetTest` | `getPetWithNegativeId_returns404OrBadRequest` | `GET /pet/{petId}` | `petId = -1` | 404 or 400 |
| N5 | `CreatePetTest` | `createPetWithEmptyBody_returns405OrBadRequest` | `POST /pet` with `{}` | Empty JSON object | 405 or 400 |

> **Note on Petstore API:** The public Petstore (https://petstore.swagger.io/v2) is a shared, unstable test server. Negative test assertions must use `assertThat(statusCode, anyOf(is(400), is(404)))` where the API behaviour is ambiguous. Never hard-code a single expected code for negative paths on this server.

---

## Task 1: Maven project skeleton + `pom.xml`

**Files:**
- Create: `api-automation/pom.xml`

**Step 1: Create `api-automation/` directory and navigate into it**

```bash
cd /Users/sinankanat/Desktop/insider-one-qa-assessment
mkdir -p api-automation/src/main/java/com/insiderone/qa/{client,model,factory}
mkdir -p api-automation/src/test/java/com/insiderone/qa/tests/pet
mkdir -p api-automation/src/test/resources
```

**Step 2: Verify structure**

```bash
find api-automation/src -type d | sort
```

Expected: 8 directories printed.

**Step 3: Write `api-automation/pom.xml`**

The pom.xml must declare:

```xml
<!-- groupId -->    com.insiderone.qa
<!-- artifactId --> api-automation
<!-- version -->    1.0.0-SNAPSHOT
<!-- packaging -->  jar
<!-- java.version property --> 17
```

Dependencies (exact versions — do not upgrade without testing):

| Dependency | GroupId | ArtifactId | Version | Scope |
|---|---|---|---|---|
| RestAssured | `io.rest-assured` | `rest-assured` | `5.4.0` | test |
| JUnit 5 API | `org.junit.jupiter` | `junit-jupiter-api` | `5.10.2` | test |
| JUnit 5 Engine | `org.junit.jupiter` | `junit-jupiter-engine` | `5.10.2` | test |
| JUnit 5 Params | `org.junit.jupiter` | `junit-jupiter-params` | `5.10.2` | test |
| Jackson Databind | `com.fasterxml.jackson.core` | `jackson-databind` | `2.17.0` | compile |
| Allure JUnit5 | `io.qameta.allure` | `allure-junit5` | `2.25.0` | test |
| Allure RestAssured | `io.qameta.allure` | `allure-rest-assured` | `2.25.0` | test |

Plugins:

| Plugin | GroupId | ArtifactId | Version | Config |
|---|---|---|---|---|
| Maven Compiler | `org.apache.maven.plugins` | `maven-compiler-plugin` | `3.12.1` | source/target = 17 |
| Maven Surefire | `org.apache.maven.plugins` | `maven-surefire-plugin` | `3.2.5` | `**/*Test.java` |
| Allure Maven | `io.qameta.allure` | `allure-maven` | `2.12.0` | reportVersion = 2.25.0 |

**Why `allure-rest-assured`?** It adds request/response logging to every Allure step automatically — no manual logging needed in tests.

**Step 4: Verify Maven resolves dependencies**

```bash
cd api-automation && mvn dependency:resolve -q
```

Expected: BUILD SUCCESS (no errors). First run downloads ~50 MB of dependencies.

**Step 5: Commit**

```bash
cd /Users/sinankanat/Desktop/insider-one-qa-assessment
git add api-automation/pom.xml api-automation/src
git commit -m "chore: scaffold api-automation Maven project with dependencies"
```

---

## Task 2: Model classes — `Pet`, `Category`, `Tag`

**Files:**
- Create: `api-automation/src/main/java/com/insiderone/qa/model/Category.java`
- Create: `api-automation/src/main/java/com/insiderone/qa/model/Tag.java`
- Create: `api-automation/src/main/java/com/insiderone/qa/model/Pet.java`

**Why `main/java` and not `test/java`?** Models are shared between the client (serialisation) and tests (assertions). Keeping them in `main` makes them a proper production-like dependency rather than test-scoped.

**Step 1: Write `Category.java`**

Fields: `long id`, `String name`
Annotations: `@JsonIgnoreProperties(ignoreUnknown = true)` on the class
Constructors: no-arg + all-args
Getters/setters for both fields

**Step 2: Write `Tag.java`**

Fields: `long id`, `String name`
Same annotations and constructor pattern as `Category`.

**Step 3: Write `Pet.java`**

Fields:
- `long id`
- `Category category`
- `String name` (required by API)
- `List<String> photoUrls` (required by API, can be empty list)
- `List<Tag> tags`
- `String status` (enum values: "available", "pending", "sold")

Annotations: `@JsonIgnoreProperties(ignoreUnknown = true)` on class
Constructors: no-arg + all-args
Getters/setters for all fields

**Step 4: Verify compilation**

```bash
cd api-automation && mvn compile -q
```

Expected: BUILD SUCCESS

**Step 5: Stage models — commit after Tasks 3, 4, 5 are done (single logical commit)**

```bash
# Do NOT commit yet — stage models and continue to Task 3
git add api-automation/src/main/java/com/insiderone/qa/model/
```

---

## Task 3: `PetFactory`

**Files:**
- Create: `api-automation/src/main/java/com/insiderone/qa/factory/PetFactory.java`

**Why static factory methods?** Tests call `PetFactory.availablePet()` — no instantiation, no builder chain. Keeps test code minimal and readable.

**Why `System.currentTimeMillis()` as ID?** The Petstore API requires a numeric ID. Using the current timestamp guarantees uniqueness across test runs and is readable in logs. We call it once at factory invocation, not at class load time, so two rapid calls yield different IDs.

**Step 1: Write `PetFactory.java`**

The class must have these static methods:

| Method | Returns | Description |
|---|---|---|
| `availablePet()` | `Pet` | Name="TestDog", status="available", unique ID, empty photoUrls list |
| `pendingPet()` | `Pet` | Name="TestCat", status="pending", unique ID |
| `soldPet()` | `Pet` | Name="TestBird", status="sold", unique ID |
| `petWithId(long id)` | `Pet` | Same as `availablePet()` but with caller-supplied ID (for update/delete tests that need a known ID) |
| `petWithStatus(String status)` | `Pet` | Generic factory accepting any status string (used in parameterised tests) |

All methods must:
- Set `photoUrls` to `Collections.emptyList()` (API requires the field, can be empty)
- Set `tags` to `Collections.emptyList()`
- NOT set `category` (optional field — tests that need it will use `petWithId` + manual setter)

**Step 2: Verify compilation**

```bash
cd api-automation && mvn compile -q
```

Expected: BUILD SUCCESS

**Step 3: Stage factory — still no commit yet**

```bash
git add api-automation/src/main/java/com/insiderone/qa/factory/
```

---

## Task 4: `BaseClient`

**Files:**
- Create: `api-automation/src/main/java/com/insiderone/qa/client/BaseClient.java`

**Why a BaseClient?** Centralises RestAssured's `RequestSpecification` configuration. If the base URL changes (e.g. staging vs prod), you change it in one place. Tests never call `RestAssured.given()` directly.

**Step 1: Write `BaseClient.java`**

The class must:
- Declare a `protected` static final `BASE_URL = "https://petstore.swagger.io/v2"`
- In a `static {}` block, call `RestAssured.baseURI = BASE_URL`
- Declare a `protected RequestSpecification requestSpec()` method that returns:
  ```
  RestAssured.given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .filter(new AllureRestAssured())  // from allure-rest-assured
  ```
- No constructor (utility-style base class — subclasses extend it)

**Why `AllureRestAssured` filter here?** Every request routed through `requestSpec()` is automatically attached to the Allure report as a step with full request/response details. No per-test annotation needed.

**Step 2: Verify compilation**

```bash
cd api-automation && mvn compile -q
```

Expected: BUILD SUCCESS

**Step 3: Stage BaseClient — still no commit yet**

```bash
git add api-automation/src/main/java/com/insiderone/qa/client/BaseClient.java
```

---

## Task 5: `PetClient`

**Files:**
- Create: `api-automation/src/main/java/com/insiderone/qa/client/PetClient.java`

**Why PetClient?** Tests must not contain HTTP verbs. `PetClient` is the single place knowing about `/pet` paths, HTTP methods, and response extraction. Tests only call descriptive methods like `petClient.create(pet)`.

**Step 1: Write `PetClient.java`**

Class extends `BaseClient`. Declare these public methods (return types are `Response` from RestAssured — tests can then assert on status code and extract body):

| Method | HTTP | Path | Body | Description |
|---|---|---|---|---|
| `create(Pet pet)` | POST | `/pet` | `pet` | Serialise pet to JSON, POST |
| `getById(long id)` | GET | `/pet/{id}` | — | Path param |
| `update(Pet pet)` | PUT | `/pet` | `pet` | Full replacement |
| `delete(long id)` | DELETE | `/pet/{id}` | — | Path param |
| `findByStatus(String status)` | GET | `/pet/findByStatus` | — | Query param `status` |

All methods call `requestSpec()` (from `BaseClient`) and `.when().<verb>().then().extract().response()`.

**Step 2: Write a smoke compilation test** — create a temporary file `api-automation/src/test/java/com/insiderone/qa/tests/pet/SmokeCompileTest.java` containing one test that instantiates `PetClient`:

```java
@Test
void petClientInstantiates() {
    assertNotNull(new PetClient());
}
```

**Step 3: Run the smoke test**

```bash
cd api-automation && mvn test -Dtest=SmokeCompileTest -q
```

Expected: `Tests run: 1, Failures: 0, Errors: 0`

**Step 4: Delete the smoke test file** (it served its purpose)

```bash
rm api-automation/src/test/java/com/insiderone/qa/tests/pet/SmokeCompileTest.java
```

**Step 5: Commit — Tasks 2–5 together as one logical unit**

```bash
cd /Users/sinankanat/Desktop/insider-one-qa-assessment
git add api-automation/src/main/java/com/insiderone/qa/client/PetClient.java
git commit -m "feat: add pet model POJOs, PetFactory, BaseClient and PetClient"
```

---

## Task 6: `CreatePetTest` — Positive scenarios P1, P2 + Negative N5

**Files:**
- Create: `api-automation/src/test/java/com/insiderone/qa/tests/pet/CreatePetTest.java`

**Step 1: Write failing tests (compilation will fail — classes don't exist yet? No, they do by now. But tests will fail against the live API if run.)**

The test class must:
- Have `PetClient petClient` instantiated in `@BeforeEach`
- Have `Pet createdPet` field set in some tests to allow `@AfterEach` cleanup
- In `@AfterEach`: call `petClient.delete(createdPet.getId())` if `createdPet != null`, then reset `createdPet = null`

**Why `@AfterEach` cleanup?** Petstore is a shared public server. Without cleanup, previous test runs leave thousands of ghost pets and GET/findByStatus results become polluted, causing false positives.

Test P1 — `createPetWithAllFields_returns200`:
```
Pet pet = PetFactory.availablePet();
// set category, add one tag manually
Response response = petClient.create(pet);
assertThat(response.statusCode(), is(200));
assertThat(response.jsonPath().getLong("id"), is(pet.getId()));
assertThat(response.jsonPath().getString("name"), is(pet.getName()));
createdPet = pet;
```

Test P2 — `createPetWithMinimalFields_returns200`:
```
Pet pet = new Pet();
pet.setId(System.currentTimeMillis());
pet.setName("MinimalPet");
pet.setPhotoUrls(Collections.emptyList());
Response response = petClient.create(pet);
assertThat(response.statusCode(), is(200));
createdPet = pet;
```

Test N5 — `createPetWithEmptyBody_returns405OrBadRequest`:
```
Pet emptyPet = new Pet();  // no fields set at all
Response response = petClient.create(emptyPet);
assertThat(response.statusCode(), anyOf(is(400), is(405), is(500)));
// no cleanup needed — likely not persisted
```

**Step 2: Run tests**

```bash
cd api-automation && mvn test -Dtest=CreatePetTest -v
```

Expected: `Tests run: 3, Failures: 0, Errors: 0`

> If the Petstore server is temporarily down, re-run once. If it fails consistently, check https://petstore.swagger.io/ in a browser first.

**Step 3: Stage — commit after GetPetTest and UpdatePetTest are also done**

```bash
git add api-automation/src/test/java/com/insiderone/qa/tests/pet/CreatePetTest.java
```

---

## Task 7: `GetPetTest` — Positive P3, Negative N1, N4

**Files:**
- Create: `api-automation/src/test/java/com/insiderone/qa/tests/pet/GetPetTest.java`

**Step 1: Write the test class**

`@BeforeEach`: create a pet via `petClient.create(PetFactory.availablePet())`, extract the returned pet and store as `petId` field.
`@AfterEach`: `petClient.delete(petId)` to clean up.

Test P3 — `getPetByValidId_returns200WithCorrectData`:
```
Response response = petClient.getById(petId);
assertThat(response.statusCode(), is(200));
assertThat(response.jsonPath().getLong("id"), is(petId));
assertThat(response.jsonPath().getString("status"), is("available"));
```

Test N1 — `getPetWithNonExistentId_returns404`:
```
Response response = petClient.getById(Long.MAX_VALUE);
assertThat(response.statusCode(), is(404));
// no cleanup needed
```

Test N4 — `getPetWithNegativeId_returns404OrBadRequest`:
```
Response response = petClient.getById(-1L);
assertThat(response.statusCode(), anyOf(is(400), is(404)));
```

**Step 2: Run tests**

```bash
cd api-automation && mvn test -Dtest=GetPetTest -v
```

Expected: `Tests run: 3, Failures: 0, Errors: 0`

**Step 3: Stage GetPetTest**

```bash
git add api-automation/src/test/java/com/insiderone/qa/tests/pet/GetPetTest.java
```

---

## Task 8: `UpdatePetTest` — Positive P4, P5

**Files:**
- Create: `api-automation/src/test/java/com/insiderone/qa/tests/pet/UpdatePetTest.java`

**Step 1: Write the test class**

`@BeforeEach`: Create a pet, store the `long petId`.
`@AfterEach`: Delete pet by `petId`.

Test P4 — `updatePetName_returns200AndNamePersists`:
```
Pet updatePayload = PetFactory.petWithId(petId);
updatePayload.setName("UpdatedName");
Response putResponse = petClient.update(updatePayload);
assertThat(putResponse.statusCode(), is(200));

Response getResponse = petClient.getById(petId);
assertThat(getResponse.jsonPath().getString("name"), is("UpdatedName"));
```

Test P5 — `updatePetStatus_returns200AndStatusPersists`:
```
Pet updatePayload = PetFactory.petWithId(petId);
updatePayload.setStatus("sold");
Response putResponse = petClient.update(updatePayload);
assertThat(putResponse.statusCode(), is(200));

Response getResponse = petClient.getById(petId);
assertThat(getResponse.jsonPath().getString("status"), is("sold"));
```

**Step 2: Run tests**

```bash
cd api-automation && mvn test -Dtest=UpdatePetTest -v
```

Expected: `Tests run: 2, Failures: 0, Errors: 0`

**Step 3: Commit — CreatePet + GetPet + UpdatePet as one logical group**

```bash
cd /Users/sinankanat/Desktop/insider-one-qa-assessment
git add api-automation/src/test/java/com/insiderone/qa/tests/pet/UpdatePetTest.java
git commit -m "test: add create, get and update pet test scenarios"
```

---

## Task 9: `DeletePetTest` — Positive P6, P7, Negative N2

**Files:**
- Create: `api-automation/src/test/java/com/insiderone/qa/tests/pet/DeletePetTest.java`

**Step 1: Write the test class**

`@BeforeEach`: Create a pet, store `petId`. Set a `boolean petAlreadyDeleted = false` flag.
`@AfterEach`: If `!petAlreadyDeleted`, call `petClient.delete(petId)`.

Test P6 — `deleteExistingPet_returns200`:
```
Response response = petClient.delete(petId);
assertThat(response.statusCode(), is(200));
petAlreadyDeleted = true;
```

Test P7 — `deletedPet_isNoLongerRetrievable`:
```
petClient.delete(petId);
petAlreadyDeleted = true;
Response getResponse = petClient.getById(petId);
assertThat(getResponse.statusCode(), is(404));
```

Test N2 — `deleteNonExistentPet_returns404`:
```
long nonExistentId = Long.MAX_VALUE - 1;
Response response = petClient.delete(nonExistentId);
assertThat(response.statusCode(), is(404));
```

**Step 2: Run tests**

```bash
cd api-automation && mvn test -Dtest=DeletePetTest -v
```

Expected: `Tests run: 3, Failures: 0, Errors: 0`

**Step 3: Stage DeletePetTest — commit after FindByStatusTest**

```bash
git add api-automation/src/test/java/com/insiderone/qa/tests/pet/DeletePetTest.java
```

---

## Task 10: `FindByStatusTest` — Positive P8–P10, Negative N3

**Files:**
- Create: `api-automation/src/test/java/com/insiderone/qa/tests/pet/FindByStatusTest.java`

**Step 1: Write the test class**

`@BeforeEach`: Create one "available" pet (needed for P8 assertion that list is non-empty). Store `petId`.
`@AfterEach`: Delete by `petId`.

Test P8 — `findByStatusAvailable_returns200WithNonEmptyList`:
```
Response response = petClient.findByStatus("available");
assertThat(response.statusCode(), is(200));
List<Map<String, Object>> pets = response.jsonPath().getList("$");
assertThat(pets, not(empty()));
pets.forEach(p -> assertThat(p.get("status"), is("available")));
```

Test P9 — `findByStatusPending_returns200`:
```
Response response = petClient.findByStatus("pending");
assertThat(response.statusCode(), is(200));
assertThat(response.jsonPath().getList("$"), instanceOf(List.class));
```

Test P10 — `findByStatusSold_returns200`:
```
Response response = petClient.findByStatus("sold");
assertThat(response.statusCode(), is(200));
assertThat(response.jsonPath().getList("$"), instanceOf(List.class));
```

Test N3 — `findByInvalidStatus_returns400`:
```
Response response = petClient.findByStatus("not_a_real_status_xyz");
assertThat(response.statusCode(), anyOf(is(400), is(200)));
// Petstore may return 200 with empty array — both outcomes are acceptable
// What's NOT acceptable is 500 (server crash)
assertThat(response.statusCode(), not(is(500)));
```

**Step 2: Run tests**

```bash
cd api-automation && mvn test -Dtest=FindByStatusTest -v
```

Expected: `Tests run: 4, Failures: 0, Errors: 0`

**Step 3: Run full test suite**

```bash
cd api-automation && mvn test
```

Expected: `Tests run: 15, Failures: 0, Errors: 0, Skipped: 0`

**Step 4: Commit — DeletePet + FindByStatus as one logical group**

```bash
cd /Users/sinankanat/Desktop/insider-one-qa-assessment
git add api-automation/src/test/java/com/insiderone/qa/tests/pet/FindByStatusTest.java
git commit -m "test: add delete and find-by-status pet test scenarios"
```

---

## Task 11: Allure configuration + local report generation

**Files:**
- Create: `api-automation/src/test/resources/allure.properties`

**Step 1: Write `allure.properties`**

```properties
allure.results.directory=target/allure-results
```

**Why?** Without this file, `allure-junit5` still works but the results directory is implicit. Making it explicit ensures the GitHub Actions `actions/upload-artifact` step has a reliable path to reference.

**Step 2: Run tests and generate the Allure report locally**

```bash
cd api-automation
mvn test
mvn allure:report
```

Expected: `target/site/allure-maven-plugin/index.html` file created.

**Step 3: Open report to verify it works (manual verification)**

```bash
open target/site/allure-maven-plugin/index.html
```

Expected: Browser opens showing Allure dashboard with 15 test results, all green.

**Step 4: Stage allure.properties — commit together with Dockerfile in Task 12**

```bash
git add api-automation/src/test/resources/allure.properties
```

---

## Task 12: `Dockerfile`

**Files:**
- Create: `api-automation/Dockerfile`

**Step 1: Write `api-automation/Dockerfile`**

Strategy: Multi-stage build.
- **Stage 1 (`builder`):** Uses `maven:3.9-eclipse-temurin-17` base. Copies `pom.xml` first (to leverage Docker layer caching for dependency downloads), then copies `src/`. Runs `mvn test -q`.
- **Stage 2:** Not needed for test containers — tests run and exit in Stage 1. The final "image" is the builder stage.

```dockerfile
# Stage 1: Build and run tests
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first — Docker caches this layer if pom.xml is unchanged
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and run tests
COPY src/ ./src/
RUN mvn test

# Copy Allure results out (accessible via docker cp or volume mount)
# Results are in /app/target/allure-results/
```

**Why `dependency:go-offline` as a separate layer?** If only `src/` changes (not `pom.xml`), Docker reuses the dependency download layer, saving 2–3 minutes per CI run.

**Step 2: Build the Docker image locally to verify**

```bash
cd api-automation && docker build -t api-automation:local .
```

Expected: Build completes with all 15 tests passing. Final line: `Successfully tagged api-automation:local`

> This step runs all tests inside Docker — it confirms the container environment matches local.

**Step 3: Commit — allure.properties + Dockerfile together**

```bash
cd /Users/sinankanat/Desktop/insider-one-qa-assessment
git add api-automation/Dockerfile
git commit -m "chore: add Allure config and Dockerfile for containerised test execution"
```

---

## Task 13: GitHub Actions workflow

**Files:**
- Create: `.github/workflows/api-automation.yml` ← repo root, NOT inside `api-automation/`

> **Critical:** GitHub Actions discovers workflows **only** from the repository root `.github/workflows/`. Do not create this file inside `api-automation/.github/` — it would be silently ignored.

**Step 1: Ensure the root `.github/workflows/` directory exists**

```bash
cd /Users/sinankanat/Desktop/insider-one-qa-assessment
ls .github/workflows/
```

Expected: directory exists (created in the monorepo skeleton plan). If missing: `mkdir -p .github/workflows`

**Step 2: Write `.github/workflows/api-automation.yml`**

The workflow must:

```yaml
name: API Automation Tests

on:
  push:
    paths:
      - 'api-automation/**'
      - '.github/workflows/api-automation.yml'   # re-run when workflow itself changes
  pull_request:
    paths:
      - 'api-automation/**'
      - '.github/workflows/api-automation.yml'
  workflow_dispatch:    # allow manual trigger from GitHub UI

jobs:
  test:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: api-automation   # all run steps resolve paths relative to this

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven         # caches ~/.m2 between runs

      - name: Run tests
        run: mvn test

      - name: Generate Allure report
        if: always()           # generate even if tests fail
        run: mvn allure:report

      - name: Upload Allure results (raw)
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: allure-results
          path: api-automation/target/allure-results/
          retention-days: 30

      - name: Upload Allure report (HTML)
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: allure-report
          path: api-automation/target/site/allure-maven-plugin/
          retention-days: 30

      # OPTIONAL: publish HTML report to GitHub Pages on main branch
      # Useful for making reports browsable without downloading an artifact.
      # Can be removed if you prefer artifact-only reporting.
      - name: Publish Allure report to GitHub Pages
        if: github.ref == 'refs/heads/main' && always()
        uses: peaceiris/actions-gh-pages@v4
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: api-automation/target/site/allure-maven-plugin
          destination_dir: allure-report
```

**Why `paths` filter?** Changes to `load-testing/` or `ui-automation/` won't trigger API tests. Adding `.github/workflows/api-automation.yml` itself to the filter ensures that pipeline changes are validated immediately.

**Why `working-directory: api-automation`?** All `run` steps (`mvn test`, `mvn allure:report`) resolve from the module directory. Without this, the runner would look for `pom.xml` at the repo root and fail.

**Why `if: always()` on report steps?** Allure reports are most useful when tests fail. `always()` ensures artifacts are uploaded regardless of `mvn test` exit code.

**Why `peaceiris/actions-gh-pages` (optional)?** Pushes the HTML report to the `gh-pages` branch, making it browsable at `https://<org>.github.io/<repo>/allure-report`. Remove this step if you want to keep CI simple.

**Step 3: Verify YAML syntax**

```bash
cd /Users/sinankanat/Desktop/insider-one-qa-assessment
python3 -c "import yaml; yaml.safe_load(open('.github/workflows/api-automation.yml'))" && echo "YAML valid"
```

Expected: `YAML valid`

**Step 4: Commit**

```bash
cd /Users/sinankanat/Desktop/insider-one-qa-assessment
git add .github/workflows/api-automation.yml
git commit -m "ci: add GitHub Actions workflow for API automation with Allure report publishing"
```

---

## Task 14: Module `README.md`

**Files:**
- Modify: `api-automation/README.md` (replace placeholder with full documentation)

**Step 1: Overwrite `api-automation/README.md`**

The README must contain:
- Module overview
- Tech stack table with versions
- Prerequisites section (Java 17, Maven 3.9, Docker)
- Local run instructions: `mvn test`, `mvn allure:report`, `open target/site/allure-maven-plugin/index.html`
- Docker run instructions: `docker build -t api-automation:local . && docker run api-automation:local`
- Folder structure diagram
- Test scenario list (link to the plan doc)
- CI badge: `![API Tests](https://github.com/<org>/insider-one-qa-assessment/actions/workflows/api-automation.yml/badge.svg)`

**Step 2: Commit**

```bash
cd /Users/sinankanat/Desktop/insider-one-qa-assessment
git add api-automation/README.md
git commit -m "docs: update api-automation README with full setup and run instructions"
```

---

## Task 15: Final end-to-end verification

**Step 1: Run full test suite one final time**

```bash
cd api-automation && mvn clean test
```

Expected:
```
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

**Step 2: Verify git log is clean**

```bash
git log --oneline -10
```

Expected: 7 new commits on top of the initial commit:
```
ci: add GitHub Actions workflow for API automation with Allure report publishing
docs: update api-automation README with full setup and run instructions
chore: add Allure config and Dockerfile for containerised test execution
test: add delete and find-by-status pet test scenarios
test: add create, get and update pet test scenarios
feat: add pet model POJOs, PetFactory, BaseClient and PetClient
chore: scaffold api-automation Maven project with dependencies
```

**Step 3: Verify directory structure**

```bash
# Verify module structure
find api-automation -not -path '*/target/*' | sort

# Verify workflow is at repo root (NOT inside api-automation/)
ls .github/workflows/api-automation.yml
```

Expected: `api-automation/` contains all source files, and `.github/workflows/api-automation.yml` exists at repo root.

**Step 4: Done**

The `api-automation` module is complete and production-ready.
