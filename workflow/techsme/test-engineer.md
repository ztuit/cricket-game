# Test Engineering Strategy — Cricket Game
**Status:** draft

---
## Full Technical Detail

### Testability review (mode: initial_review)

| Concern | Observation | Recommendation |
|---|---|---|
| **Domain purity** | Domain layer is pure Kotlin with zero Android dependencies. This is the strongest testability win in the architecture. Unit tests run on JVM, no emulator needed, fast feedback loop. | Domain-layer tests should be the bulk of the suite. They run in seconds, not minutes. |
| **Aggregate isolation** | Aggregates (Match, Bowler, Batsman, Pitch, Field Placement) are self-contained with clear boundaries. Match references other aggregates by ID, not by holding their state directly. Each aggregate can be instantiated and tested in isolation. | Feature Owner should test each aggregate independently. No need to build a full match graph to test a Bowler's invariants. |
| **Value object immutability** | All value objects (BallCharacteristics, ShotSelection, Outcome, SurfaceCondition, etc.) are data-class style, immutable, with range validation. They are inherently testable: construct, assert, done. | Value object tests are cheap to write. Cover boundary validation (e.g., pace = 0, pace = 1, pace = -0.01) early. |
| **Rendering seam** | `GameRenderer` interface decouples game logic from rendering. This is the primary mock boundary — tests verify the engine sends the right data to the renderer without touching Android Canvas or Compose. | Mock `GameRenderer` in integration tests. Verify data passed to the interface, not pixels on screen. |
| **Probability model determinism** | ADR-005 explicitly states randomness is injected via a seed, not `Math.random()`. This is critical — without it, the probability model would be untestable. | Every test that touches outcome resolution must use a fixed seed. The seed is part of the test fixture, not the production code's concern. |
| **Surface physics complexity** | Pitch has a zone grid, each zone with degradation, moisture, and roughness. Degradation is monotonically non-decreasing. Weather affects degradation. This is the most complex model to test. | Needs dedicated test fixtures for known surface states. Test degradation invariants separately from delivery resolution. The surface model is pure computation — no mocking needed, just careful setup. |
| **Domain events as Flows** | Domain events emitted as Kotlin Flows. In tests, Flows can be collected synchronously via `turbine` or `first()` without needing Android lifecycle. | Use `kotlinx-coroutines-test` and Turbine for Flow testing. Verify events emitted in correct order with correct payloads. |
| **Repository pattern** | Repository interfaces separate domain from data. For MVP 1, only Room implementation exists. Tests of domain logic don't need Room — mock the repository interface if testing a use case that depends on persistence. | For domain-layer unit tests, construct aggregates directly. For ViewModel/integration tests, use fake or in-memory repository implementations. |
| **No server component** | MVP 1 is fully offline. No network mocking, no API stubs, no latency simulation needed. This simplifies the test environment enormously. | No HTTP mocking framework needed until MVP 4. Keep the test environment lean. |
| **Cross-context dependencies** | Delivery → Player, Delivery → Field, Delivery → Pitch. These are "uses" relationships — Delivery references other aggregates' data but doesn't own them. | Test Delivery resolution with injected player stats, field placement, and surface conditions. These are test inputs, not collaborators to mock. |

### Test framework

| Concern | Decision | Rationale |
|---|---|---|
| **Framework / runner** | **JUnit 5** (Jupiter) for unit tests. **kotlinx-coroutines-test** for coroutine/Flow testing. **Turbine** for Flow assertion. | JUnit 5 is the Kotlin standard. Coroutines-test provides `runTest` and `TestDispatcher`. Turbine gives clean Flow assertion API. No framework switch needed — this matches the language/stack already in use. |
| **Fixture / test-data strategy** | Builder functions for domain objects (e.g., `aMatch()`, `aBowler()`, `aDelivery()`). Named parameters with sensible defaults. One builder per aggregate root and key value object. | Builders extracted from Increment 1's real patterns. Feature Owner shouldn't need to construct 10 objects to test one delivery. Builders should have meaningful defaults that produce valid domain objects. |
| **Mocking approach** | **MockK** for Kotlin-native mocking. Mock only at system boundaries: `GameRenderer`, repository interfaces. Do not mock domain objects — construct them directly. | MockK is idiomatic Kotlin, supports coroutine mocking, and handles sealed classes well. Mocking is for boundaries (rendering, persistence), not for domain logic. |
| **Android instrumented tests** | Separate `androidTest` source set. Only for Android-specific concerns: Room DAO tests, Compose UI tests, gesture handling. | Domain-layer tests never need an instrumented test. Instrumented tests are slow and expensive — use them only when the thing under test requires an Android runtime. |
| **CI integration** | `./gradlew test` on every push via GitHub Actions (already in SE architecture). Domain tests run first (fast). Instrumented tests run on a separate, slower stage. | Coordinate with Platform Engineer's pipeline — test stage runs unit tests as a gate. Instrumented tests are informational in MVP 1 (not blocking until the test device environment is stable). |

### Coverage reporting

| Concern | Decision |
|---|---|
| **Tool** | **JaCoCo** (Java/Kotlin code coverage). Integrates with Gradle, produces HTML reports, supported by GitHub Actions. |
| **Threshold (if any)** | No hard threshold for MVP 1 — coverage is a signal, not a gate. As the domain layer stabilises, raise to 80% line coverage on `domain.*` packages. Android/UI layer intentionally lower. |
| **Reported at** | Per-increment (each push produces a report). Trend visible across increments via CI artifact retention. |
| **Location** | Reports archived as GitHub Actions artifacts. Summary posted to PR comments via `jacoco-report-upload-action` or equivalent. |

### Defect and regression tracking

- **Defect log location:** `workflow/quality/defects.md`
- **Notification target on new defect:** Coder (implementation defect) or Delivery Lead (unclear ownership)
- **Regression definition:** a previously-passing test now fails without an intentional, documented behaviour change
- **Process:**
  1. Test Engineer creates defect entry in `workflow/quality/defects.md` using the standard format
  2. For regressions: notify immediately (same session), do not batch
  3. For new defects: notify Coder or DL as appropriate within the same session
  4. Defect owner updates status in `workflow/quality/defects.md` once resolved
  5. Regressions are always `severity: high` or above — a known-good behaviour broke

### Platform requirements (first-class backlog items)

| Requirement | Priority | From | Acceptance criteria |
|---|---|---|---|
| **TEST-001 — Test framework and fixtures established** | should | MVP 1 Increment 1 | JUnit 5 + kotlinx-coroutines-test + Turbine + MockK in place. Builder functions for Match, Bowler, Batsman, Pitch, Field Placement, BallCharacteristics, ShotSelection, Outcome. Feature Owner can write a new test using shared builders without copy-pasting setup. |
| **TEST-002 — Coverage reporting** | should | MVP 1 (after Increment 1) | JaCoCo integrated into Gradle test stage. HTML report produced on each CI run. Domain-layer coverage visible separately from Android-layer coverage. Trend tracked across increments. |
| **TEST-003 — Defect log established** | must | MVP 1 | `workflow/quality/defects.md` in place with standard entry format. At least one defect (real or seeded example) recorded end-to-end with notification to owner. |

### Feedback to Senior Engineer

**The architecture is highly testable. No structural changes needed.** The decisions to keep the domain layer pure Kotlin, inject randomness via seed, and decouple rendering via `GameRenderer` are exactly right for testability. Specific observations:

1. **The probability model seed mechanism is the most important testability decision in the architecture.** Without it, outcome resolution would be non-deterministic and untestable. Ensure the seed is a first-class parameter, not an afterthought — it flows through from match creation to delivery resolution.

2. **Surface physics model complexity.** The Pitch aggregate (zone grid × degradation × moisture × roughness × weather × ball age) is the most complex thing to test. It needs a clear specification of how each factor interacts — not just the code, but a human-readable description the Feature Owner can write tests against. If the physics model is underspecified, the tests will be testing assumptions, not requirements.

3. **The `GameRenderer` interface should be designed with test assertions in mind.** It should receive structured data (what to render) not imperative calls (draw line from A to B). The current architecture implies this (ADR-001), but confirm the interface signature is query-friendly: "here is the ball trajectory" not "draw the ball at x,y".

4. **Room DAO tests need a clear boundary.** Domain-layer tests never touch Room. Data-layer tests (DAO queries, migrations) are instrumented tests that run on the slower CI stage. Don't let Room leak into domain test setup.

5. **Match serialisation for resume.** The NFR says "Match aggregate is serializable" and "Resume logic on app start." This means the Match aggregate must survive a round-trip through Room (serialize → store → retrieve → deserialize). This is a testable property — add a test that serialises a mid-match state, deserialises it, and verifies invariants hold.

---
## Plain-Language Summary

### Risk
The game's physics model — how the pitch changes over 20 overs, how weather affects the ball, how different surfaces behave — is the hardest part to test. Not because it's untestable (it's pure computation, which is ideal), but because it needs a clear specification of how all the factors interact. If the physics rules aren't written down clearly, the Feature Owner will end up testing guesses, and the probability model will produce outcomes that feel random to the player.

### Constraint
Everything runs locally. No server means no integration test environment, no API contract tests, no load tests. Testing is entirely unit tests (domain logic) and manual device testing (UI feel). This is fine for MVP 1, but it means the "does it feel right" validation depends on humans playing the game on real devices — automated tests can verify correctness, not fun.

### Vision impact
The test platform decisions here are lightweight and non-binding. If the team outgrows JUnit 5 or needs a different mocking library, switching is cheap. The bigger risk is not the tooling but the physics model specification — if that's wrong, every delivery outcome feels wrong, and no amount of test coverage fixes a misunderstood domain.

### Recommendation
Start with domain-layer unit tests from Increment 1. They run in seconds, need no emulator, and give fast feedback. Build the test fixtures (builders for Match, Bowler, Delivery, etc.) as the Feature Owner writes the first tests — don't speculatively build a fixture library before knowing what's actually needed. Add JaCoCo after the first increment to start tracking coverage trends. The defect log is mandatory from day one.

---
## PO Approval
**Status:** approved
**Date:** 2026-08-20
**Notes:** Test engineering strategy is compatible with the product vision. The testability review confirms the architecture's strongest property — domain purity means the game logic is fully unit-testable without Android emulator overhead. The physics model complexity observation is well-taken: the pitch model is the deepest feature and needs a clear specification before the Feature Owner can write meaningful tests against requirements rather than assumptions. The recommendation to build fixtures from Increment 1's real patterns (not speculatively) is the right approach for a solo developer. The three platform backlog items (TEST-001 through TEST-003) are appropriately prioritised — the defect log is mandatory from day one, while test framework and coverage reporting follow from actual need.
