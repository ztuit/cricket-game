# Coder Briefing — INCR-001
**Dispatched:** 2026-08-21
**By:** Delivery Lead
**Increment:** `workflow/requirements/mvp-1/increments/INCR-001.md`

---

## What you are doing

Implementing the core domain objects to make the Feature Owner's tests pass. You write implementation code ONLY — never modify tests. If a test is wrong, raise it with the Feature Owner via DL.

## What to read (in this order)

1. `workflow/requirements/mvp-1/increments/INCR-001.md` — acceptance criteria and technical notes
2. `workflow/techsme/ddd.md` — ubiquitous language (your code MUST use these terms)
3. `workflow/techsme/senior-engineer.md` — architecture, ADRs, technology choices

## Domain objects to implement

| Object | Bounded Context | Key invariants |
|---|---|---|
| **Match** | Match | Max 20 overs, max 10 wickets, owns InningsProgress |
| **InningsProgress** | Match | Wickets ≤ 10, balls ≤ 6 per over, score ≥ 0 |
| **TossResult** | Match | Winner (Player/AI), Decision (Bat/Field) |
| **Bowler** | Player | BowlerType immutable, ExperienceClass immutable, max 4 overs |
| **BowlerStats** | Player | All values in [0, 1] |
| **Batsman** | Player | Skills immutable for match |
| **BatsmanStats** | Player | All values in [0, 1] |
| **Ground** | Pitch | Name, location, default weather |
| **Pitch** | Pitch | Zone grid with SurfaceCondition per zone |
| **SurfaceCondition** | Pitch | All values in [0, 1], monotonically non-decreasing degradation |
| **Weather** | Pitch | Valid enum, humidity in [0, 1] |
| **FieldPlacement** | Field | Exactly 11 fielder positions |
| **FielderPosition** | Field | Position name, x/y coordinates |
| **CharacterInfo** | Player | Non-empty nickname and quirk |

## Value objects to implement

| Value Object | Attributes | Validation |
|---|---|---|
| **BallCharacteristics** | line (enum), length (enum), pace (0–1), spin (0–1) | Range checks |
| **Outcome** | type (enum), runs (int), dismissalType (nullable) | If Wicket → dismissalType required, runs ≥ 0 |
| **InningsProgress** | oversCompleted, ballsThisOver (0–5), wicketsFallen (0–10), currentScore, target (nullable) | Invariants enforced |

## Enums to define

- **BowlerType:** Fast, MediumFast, OffSpin, LegSpin
- **ExperienceClass:** Rookie, Established, Elite
- **DismissalType:** Bowled, Caught, LBW, Stumped, RunOut
- **Weather:** Sunny, Overcast, Humid, Cloudy
- **ShotType:** Drive, Pull, Cut, Sweep, Defensive, Leave, LegGlance, Slog, ReverseSweep, UpperCut

## Match creation flow

1. Create Ground with name, location, default Weather
2. Create Pitch with zone grid and SurfaceCondition per zone
3. Create Bowler roster (≥ 5 bowlers, covering all 4 BowlerTypes)
4. Create Batsman
5. Perform Toss → produces TossResult
6. Calculate Target based on toss outcome
7. Initialize InningsProgress (0 overs, 0 wickets, 0 runs)
8. Emit MatchStarted and TossCompleted domain events

## Domain events to emit

| Event | Payload |
|---|---|
| **MatchStarted** | matchId, groundId, weather, tossResult |
| **TossCompleted** | tossResult, battingFirst |

Events emitted via Kotlin Flows (ADR-004).

## Toss mechanic

- Coin flip determines winner (Player or AI)
- If player wins: player chooses Bat or Field
- If player loses: **AI chooses** — player does NOT get to choose
- Human explicitly confirmed this behaviour

## Target calculation (ADR-008)

- Player bats first → target = null (setting a target)
- Player chases → target = pre-calculated positive integer based on difficulty parameter

## Technical constraints

- **Pure Kotlin** — no Android dependencies whatsoever
- **ADR-002:** Domain purity. No Compose, no Room, no framework imports in domain code
- **ADR-004:** Domain events as Kotlin Flows
- **ADR-008:** Pre-calculated AI target
- **SEC-003:** Value object validation at construction — invalid inputs rejected immediately
- Code organised to reflect bounded contexts: `domain.match`, `domain.player`, `domain.field`, `domain.pitch`
- Dependencies point inward — domain logic does not depend on infrastructure
- No unnecessary abstraction — only introduce a pattern for a concrete reason

## Logging (from Ops notes)

Domain events must be logged with structured format:
- Event name
- Match ID
- Timestamp

This is the first increment where structured logging is exercised.

## What you do NOT do

- Do NOT write or modify functional tests (Feature Owner's job)
- Do NOT write PVT assertions yet (PIPELINE-003 is not done — runtime PVT comes later)
- Do NOT add Android dependencies to the domain layer
- Do NOT use synonyms for domain terms from ddd.md

## When you are done

1. All Feature Owner tests pass
2. Update the increment file with:
```markdown
## Coder sign-off — [date]
Implementation complete. All Feature Owner tests passing.
Files changed: [list]
PVT assertions added: none (PIPELINE-003 not yet available)
Notable decisions: [non-obvious choices and why]
Ready for: DL validation
```
3. Notify the Delivery Lead

## Note on PVT

PIPELINE-003 (PVT assertion framework) is not yet done. The increment's PVT acceptance criterion ("Match, Bowler, Batsman, Pitch, Field Placement can be instantiated without error") will be verified by the unit tests themselves — if they instantiate and pass, the objects work. Formal PVT assertions will be added retroactively when PIPELINE-003 lands.
