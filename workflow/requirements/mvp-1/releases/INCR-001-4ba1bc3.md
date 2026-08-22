# Release Note — INCR-001
**Commit SHA:** 4ba1bc31657d0bf88b19594d309c25305650eab8
**Deployed:** 2026-08-22 12:00 UTC
**Environment:** local (unit tests pass, domain layer has no Android dependencies)
**MVP:** 1
**Deployed by:** Coder

---
## What was deployed
Core domain objects for the cricket game: Match aggregate with toss mechanic, Bowler/Batsman models with stats, Pitch/Ground/SurfaceCondition, FieldPlacement with 11 fielder positions, InningsProgress tracking, and domain events (MatchStarted, TossCompleted). All pure Kotlin — no Android dependencies.

## PVT assertions added in this increment
| Assertion | What it checks | Failure message |
|---|---|---|
| Match instantiation | Match.create() produces valid Match with invariants | Match cannot be instantiated — check maxOvers (0-20), maxWickets (0-10), bowlerRosterIds (>=5) |
| Bowler instantiation | Bowler.create() produces valid Bowler with stats | Bowler cannot be instantiated — check nickname/quirk not blank |
| Batsman instantiation | Batsman.create() produces valid Batsman with stats | Batsman cannot be instantiated |
| Pitch instantiation | Pitch.create() produces valid Pitch with zones | Pitch cannot be instantiated |
| FieldPlacement instantiation | FieldPlacement.create() produces valid placement with 11 fielders | FieldPlacement cannot be instantiated — must have exactly 11 fielders |

**Service started successfully:** yes (domain layer — no service to start, all unit tests pass)

## How to observe it is running
- Run `./gradlew test` — all 58 domain tests pass (0 failures)
- Test results in `app/build/test-results/testDebugUnitTest/`

## How to validate it works
1. Run `./gradlew test` from project root
2. Verify BUILD SUCCESSFUL with 0 failures
3. Check test XML files: MatchCreationTest (22 tests), BowlerTest (13 tests), PitchTest (16 tests), FieldPlacementTest (7 tests)

## How to validate it fails correctly
1. Try `Match.create(maxOvers = 21)` — throws IllegalArgumentException
2. Try `Match.create(maxWickets = 11)` — throws IllegalArgumentException
3. Try `Match.create(bowlerRosterIds = listOf("b1","b2","b3","b4"))` — throws IllegalArgumentException (need >= 5)
4. Try `InningsProgress(wicketsFallen = 11)` — throws IllegalArgumentException
5. Try `SurfaceCondition(degradation = 1.1f)` — throws IllegalArgumentException
6. Try `Weather(humidity = 1.1f)` — throws IllegalArgumentException
7. Try `FieldPlacement.create(fielders = listOf())` — throws IllegalArgumentException (need exactly 11)

## Rollback procedure
1. `git revert 4ba1bc31657d0bf88b19594d309c25305650eab8`
2. `git push origin master`

## Known limitations
- No PVT startup hook wired yet (PIPELINE-004 not deployed) — PVT assertions are verified via unit tests only
- Domain events are collected in-memory via `collectEvents()` — Kotlin Flow emission not yet wired (ADR-004)
- Toss decision is random for both player and AI — no strategy logic yet
- Batsman stats are hardcoded defaults — no customization yet
- No logging implemented yet (Ops requirement) — deferred to next increment when service exists

## Deployment evidence
- Commit 4ba1bc3 pushed to master
- `./gradlew clean test` — BUILD SUCCESSFUL in 36s, 45 tasks executed
- Test results: 58 domain tests, 0 failures, 0 errors
