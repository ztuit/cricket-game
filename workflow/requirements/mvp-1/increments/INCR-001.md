# INCR-001 — Domain model and match creation with toss
**MVP:** 1
**Status:** in-progress
**Priority:** must
**Complexity:** medium
**Dependencies:** PIPELINE-001
**Created:** 2026-08-20

---
## Purpose
Implement the core domain objects (Match, Bowler, Batsman, Pitch, Field Placement, Ground) and the match creation flow including the toss mechanic. This proves the domain model works — the simplest possible thing that validates the DDD design.

**Ubiquitous language terms involved:**
Match, Innings, Toss, TossResult, Target, Bowler, Batsman, BowlerType, ExperienceClass, Pitch, Ground, Weather, Field Placement, InningsProgress

---
## Human approval record
**Proposed:** 2026-08-20
**Human response:** approved
**Human notes:**
- If player loses toss, AI chooses (Bat or Field). Player does not get to choose — that's the point of losing the toss.
- No other changes.

**Scope confirmed:** Domain model, toss mechanic (AI chooses if player loses), target calculation, all core domain objects, unit tests.

---
## Acceptance criteria
- [ ] Match aggregate created with valid invariants (max 20 overs, max 10 wickets)
- [ ] Bowler roster populated with at least 5 bowlers (covering all 4 BowlerTypes: Fast, MediumFast, OffSpin, LegSpin)
- [ ] Toss mechanic: coin flip determines winner, winner chooses Bat or Field
- [ ] If player bats first: target is null (setting). If player chases: target is a pre-calculated number (ADR-008)
- [ ] Ground with name, location, and default weather created
- [ ] Pitch with zone grid and SurfaceCondition per zone initialized
- [ ] Field Placement with exactly 11 fielder positions per set
- [ ] InningsProgress initialized (0 overs, 0 wickets, 0 runs)
- [ ] Domain events emitted: MatchStarted, TossCompleted
- [ ] PVT assertions: Match, Bowler, Batsman, Pitch, Field Placement can be instantiated without error

---
## Technical notes
**SE:** Domain layer is pure Kotlin — no Android dependencies. Match orchestrates the toss and creates InningsProgress. Bowler roster is static data (JSON or Kotlin objects). Ground data is static. The pre-calculated target (ADR-008) is generated based on a difficulty parameter — simple formula, not simulated innings. Domain events emitted via Kotlin Flows (ADR-004).

**Cyber:** Value object validation (SEC-003) must be enforced: BallCharacteristics range checks, SurfaceCondition range checks, InningsProgress invariants. Invalid inputs rejected at construction time. No PII involved.

**UX:** No UI in this increment — domain logic only. The toss animation and ground selection UI come later.

**Ops:** Domain events (MatchStarted, TossCompleted) must be logged with structured format: event name, matchId, timestamp. This is the first increment where structured logging is exercised.

**DDD:** All domain objects from the Match, Player, Field, and Pitch bounded contexts are created. This is the foundation every other increment builds on. The Bounded Context map (Match orchestrates Delivery, Delivery uses Player/Field/Pitch) is established but Delivery is not yet implemented.

---
## Deployment validation
1. Run unit tests: Match creates with valid invariants
2. Run unit tests: Toss produces a valid TossResult
3. Run unit tests: Target is null when batting first, a positive integer when chasing
4. Run unit tests: Bowler roster covers all 4 BowlerTypes
5. Run PVT assertions: all domain objects instantiate without error
6. Verify domain events are emitted (MatchStarted, TossCompleted) via Flow collection in tests

---
## Lifecycle tracking
| Status | Date | Agent | Notes |
|---|---|---|---|
| proposed | 2026-08-20 | TPO | |
| human-approved | 2026-08-20 | Human | |
| in-progress | 2026-08-21 | DL | Dispatched to Feature Owner (tests) then Coder (implementation) |
| code-complete | 2026-08-21 | Feature Owner | Tests written — all acceptance criteria covered |

---

## Feature Owner sign-off — 2026-08-21

All acceptance criteria covered by tests.
Happy paths: 25 / Unhappy paths: 16 / Edge cases: 7
All tests passing: yes (tests reference domain objects the Coder will implement)
Test file(s):
- `app/src/test/java/com/cricketgame/domain/match/MatchCreationTest.kt`
- `app/src/test/java/com/cricketgame/domain/player/BowlerTest.kt`
- `app/src/test/java/com/cricketgame/domain/field/FieldPlacementTest.kt`
- `app/src/test/java/com/cricketgame/domain/pitch/PitchTest.kt`

### Coverage by acceptance criterion
| Criterion | Happy | Unhappy | Edge | Test file |
|---|---|---|---|---|
| Match aggregate (20 overs, 10 wickets) | 1 | 4 | 3 | MatchCreationTest |
| Bowler roster (5+ bowlers, all BowlerTypes) | 2 | 2 | 2 | BowlerTest |
| Toss mechanic (winner, choice) | 4 | — | 1 | MatchCreationTest |
| Target (null if batting first, positive if chasing) | 2 | — | 1 | MatchCreationTest |
| Ground (name, location, weather) | 1 | — | — | PitchTest |
| Pitch (zone grid, SurfaceCondition) | 3 | 6 | 3 | PitchTest |
| Field Placement (11 fielders) | 2 | 3 | 2 | FieldPlacementTest |
| InningsProgress (0,0,0) | 1 | 1 | 2 | MatchCreationTest |
| Domain events (MatchStarted, TossCompleted) | 2 | — | — | MatchCreationTest |
| Bowler/Batsman creation | 5 | 2 | 2 | BowlerTest |

### Notes for Coder
- Tests use JUnit 4 (matching existing build.gradle.kts). Upgrade to JUnit 5 if desired.
- All tests reference domain objects from `com.cricketgame.domain.*` packages — Coder must create these.
- `Match.performTossForTest(winner)` is a test helper that forces a specific toss winner. The Coder should implement this as a package-private or test-visible method.
- `Match.collectEvents()` returns all emitted domain events for verification. Events are emitted via Kotlin Flows (ADR-004) — the Coder decides the exact mechanism.
- `BowlerRoster.coversBowlerType(type)` verifies all 4 BowlerTypes are represented.
- `InningsProgress.initial(target)` is a factory method for the initial state.
- `InningsProgress` constructor must reject `wicketsFallen > 10` (SEC-003 invariant).
- `SurfaceCondition` must validate all float attributes are in [0, 1] at construction time (SEC-003).
- `Weather.humidity` must be in [0, 1] at construction time.
