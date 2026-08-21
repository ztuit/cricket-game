# TEST-001 — Test framework and fixtures established
**MVP:** 1
**Status:** open
**Priority:** should
**Complexity:** medium
**Dependencies:** INCR-001
**Created:** 2026-08-20

---
## Purpose
JUnit 5, kotlinx-coroutines-test, Turbine, and MockK are in place. Builder functions for Match, Bowler, Batsman, Pitch, Field Placement, BallCharacteristics, ShotSelection, and Outcome are extracted from Increment 1's real patterns. Feature Owner can write a new test using shared builders without copy-pasting setup.

**Ubiquitous language terms involved:**
All domain objects — the builders produce valid instances of each.

---
## Acceptance criteria
- [ ] JUnit 5 configured as the test runner
- [ ] kotlinx-coroutines-test included for coroutine/Flow testing
- [ ] Turbine included for Flow assertion
- [ ] MockK included for mocking at system boundaries (GameRenderer, repositories)
- [ ] Builder functions exist for: Match, Bowler, Batsman, Pitch, Field Placement, BallCharacteristics, ShotSelection, Outcome
- [ ] Builders have meaningful defaults that produce valid domain objects
- [ ] Builders use named parameters for easy override
- [ ] Feature Owner confirms it is easier to add a test using shared builders than without them

---
## Technical notes
**SE:** Test dependencies in build.gradle.kts: junit-jupiter, kotlinx-coroutines-test, turbine, mockk. Builders live in a shared test source set (e.g., src/testFixtures/ or src/test/shared/). Builders are extracted from INCR-001's actual test patterns — not speculatively designed. This is deliberately after INCR-001, not before, per Test Engineer's guidance.

**Cyber:** None.

**UX:** None.

**Ops:** None.

**DDD:** Builders must produce objects that satisfy all domain invariants. aMatch() produces a valid Match with valid InningsProgress. aBowler() produces a Bowler with valid stats and character info. aDelivery() produces a Delivery with valid BallCharacteristics and ShotSelection.

---
## Deployment validation
1. Verify all test dependencies are in build.gradle.kts
2. Verify builder functions exist for all 8 domain object types
3. Write a new test using only builders (no manual setup) — confirm it is simpler
4. Run `./gradlew test` — all existing tests still pass
5. Feature Owner confirms builders reduce test setup effort
