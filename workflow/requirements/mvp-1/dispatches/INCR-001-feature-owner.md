# Feature Owner Briefing — INCR-001
**Dispatched:** 2026-08-21
**By:** Delivery Lead
**Increment:** `workflow/requirements/mvp-1/increments/INCR-001.md`

---

## What you are doing

Writing tests for the core domain objects and match creation flow with toss. You write tests ONLY — never implementation code. The Coder implements to make your tests pass.

## What to read (in this order)

1. `workflow/requirements/mvp-1/increments/INCR-001.md` — acceptance criteria
2. `workflow/techsme/ddd.md` — ubiquitous language (your test names MUST use these terms)
3. `workflow/techsme/senior-engineer.md` — domain layer is pure Kotlin, no Android dependencies

## Acceptance criteria to cover with tests

| # | Criterion | Test category |
|---|---|---|
| 1 | Match aggregate created with valid invariants (max 20 overs, max 10 wickets) | Happy + Edge |
| 2 | Bowler roster populated with at least 5 bowlers covering all 4 BowlerTypes | Happy + Edge |
| 3 | Toss mechanic: coin flip determines winner, winner chooses Bat or Field | Happy + Edge |
| 4 | If player bats first: target is null. If player chases: target is a positive integer | Happy + Edge |
| 5 | Ground with name, location, and default weather created | Happy |
| 6 | Pitch with zone grid and SurfaceCondition per zone initialized | Happy |
| 7 | Field Placement with exactly 11 fielder positions per set | Happy + Edge |
| 8 | InningsProgress initialized (0 overs, 0 wickets, 0 runs) | Happy |
| 9 | Domain events emitted: MatchStarted, TossCompleted | Happy |
| 10 | PVT: Match, Bowler, Batsman, Pitch, Field Placement can be instantiated | Happy |

## Test categories required

- **Happy path:** Expected successful outcomes
- **Unhappy path:** Invalid inputs rejected at construction (e.g., negative overs, wickets > 10, surface condition out of range)
- **Edge cases:** Boundary values (exactly 20 overs, exactly 10 wickets, exactly 11 fielders, surface condition at 0.0 and 1.0)

## Key domain terms (from ddd.md)

Use these EXACTLY in test names and assertions. Using a synonym is a defect.

- **Match** (not "Game" or "round")
- **Innings** (player's batting period)
- **Toss / TossResult** (coin toss outcome)
- **Target** (score to chase, null if batting first)
- **Bowler / BowlerType** (Fast, MediumFast, OffSpin, LegSpin)
- **ExperienceClass** (Rookie, Established, Elite)
- **Batsman**
- **Pitch** (the strip, not "Ground")
- **Ground** (the venue)
- **SurfaceCondition** (not "coefficient")
- **FieldPlacement** (not "field set")
- **InningsProgress** (not "match state")
- **BallCharacteristics** (line, length, pace, spin)
- **Weather** (Sunny, Overcast, Humid, Cloudy)

## Toss mechanic detail

- If player wins toss: they choose Bat or Field
- If player loses toss: AI chooses (player does NOT get to choose)
- This is the point of losing the toss — human confirmed this

## Target calculation (ADR-008)

- Player bats first → target is null (they are setting a target)
- Player chases → target is a pre-calculated positive integer based on difficulty

## Technical constraints

- Domain layer is **pure Kotlin** — no Android dependencies
- No UI in this increment — domain logic only
- Domain events via Kotlin Flows (ADR-004)
- Value object validation enforced at construction time (SEC-003)

## What you do NOT do

- Do NOT write implementation code
- Do NOT write PVT assertions (those are the Coder's responsibility)
- Do NOT modify tests to make them pass — if a test is wrong, raise it with DL

## When you are done

Update the increment file with:
```markdown
## Feature Owner sign-off — [date]
All acceptance criteria covered by tests.
Happy paths: [N] / Unhappy paths: [N] / Edge cases: [N]
All tests passing: yes
Test file(s): [paths]
```

Then notify the Delivery Lead.
