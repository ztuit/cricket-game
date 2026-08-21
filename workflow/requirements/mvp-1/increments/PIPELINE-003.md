# PIPELINE-003 — PVT assertion framework
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** medium
**Dependencies:** PIPELINE-001
**Created:** 2026-08-20

---
## Purpose
Establish the mechanism for Coders to register domain PVT assertions that run as unit tests in CI. A failed assertion prevents the service (app) from being considered healthy. This is the quality gate that every subsequent increment builds on.

**Ubiquitous language terms involved:**
None directly — this is infrastructure. PVT assertions verify domain objects (Match, Bowler, etc.) can be instantiated, but the framework itself is not domain logic.

---
## Acceptance criteria
- [ ] PVT assertions run as part of `./gradlew test` (no separate step needed)
- [ ] Failed assertion produces a test failure with a human-readable message naming what is misconfigured
- [ ] Coder documentation written: how to register a new PVT assertion (location: `app/src/test/java/.../pvt/`)
- [ ] At least one example assertion present (e.g., domain objects instantiate without error, static data loads and validates)
- [ ] PVT assertions complete in under 5 seconds total
- [ ] Hook pattern approved by SE for architectural consistency

---
## Technical notes
**SE:** PVT assertions are pure Kotlin unit tests in `app/src/test/java/.../pvt/`. They verify: domain objects can be instantiated, value object invariants hold, enum values are complete, static data (grounds, field placements) loads and validates. They run alongside Feature Owner tests in `./gradlew test`. No separate CI step needed — they ARE tests. The "hook" is simply a test package convention: anything in the `pvt` package is a PVT assertion.

**Cyber:** PVT assertion for value object validation (SEC-003) should be included as an example: verify that invalid inputs to value objects (e.g., pace outside 0–1) are rejected.

**UX:** None — no UI.

**Ops:** None — CI-only.

**DDD:** Example PVT assertions should verify: Match can be created with valid parameters, Bowler enforces max 4 overs invariant, BallCharacteristics rejects out-of-range values, SurfaceCondition enforces monotonic degradation.

---
## Deployment validation
1. Add a deliberately broken PVT assertion (e.g., assert that a Bowler can bowl 5 overs)
2. Push to a branch and verify CI fails with a human-readable message
3. Fix the assertion and verify CI passes
4. Verify all PVT assertions complete in under 5 seconds (check CI timing)
5. Read the Coder documentation and confirm a new developer could add an assertion without asking for help
