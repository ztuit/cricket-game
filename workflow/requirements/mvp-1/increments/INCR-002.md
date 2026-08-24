# INCR-002 — Delivery loop — ball delivery, batting input, outcome resolution
**MVP:** 1
**Status:** in-progress
**Priority:** must
**Complexity:** high
**Dependencies:** INCR-001
**Created:** 2026-08-20

---
## Purpose
Implement the core gameplay loop: a ball is bowled, the player selects a shot type and wrist angle, and the outcome is resolved by the probability model. This is the atomic unit of gameplay — the single delivery that everything else orbits around.

**Ubiquitous language terms involved:**
Delivery, BallCharacteristics, Shot, ShotType, WristAngle, Outcome, Dismissal, DismissalType, Line, Length, Pace, Spin, Bowler, Batsman, BowlerStats, BatsmanStats

---
## Human approval record
**Proposed:** 2026-08-22
**Human response:** approved
**Human notes:**
- Probability model should include all identified factors: bowler/batter skill, experience, pitch conditions, ball age, etc.
- Start simple (weighted lookup), iterate to more sophistication over time
- All factors already identified in the domain model (BowlerStats, BatsmanStats, SurfaceCondition, Weather, ball age)

**Scope confirmed:** Delivery aggregate, BallCharacteristics, ShotSelection, Outcome resolution with all factors, domain events.

---
## Acceptance criteria
- [ ] Delivery aggregate created with BallCharacteristics (line, length, pace, spin) and ShotSelection (shotType, wristAngle)
- [ ] 10 shot types available: Drive, Pull, Cut, Sweep, Defensive, Leave, LegGlance, Slog, ReverseSweep, UpperCut
- [ ] Wrist angle as a float value (UI input mechanism is a separate concern — this increment handles the domain)
- [ ] Outcome resolution: probability model takes (BallCharacteristics, ShotSelection, BowlerStats, BatsmanStats, SurfaceCondition) and produces an Outcome
- [ ] Outcome types: Runs (0-6), Wicket (with dismissal type), Wide, NoBall, DotBall
- [ ] Dismissal types: Bowled, Caught, LBW, Stumped, RunOut (RunOut excluded for single batsman model)
- [ ] Probability model is deterministic given a seed (ADR-005)
- [ ] Domain events emitted: DeliveryBowled, ShotPlayed, OutcomeResolved
- [ ] PVT assertions: Delivery can be created, Outcome enforces invariants (wicket requires dismissal type)

---
## Technical notes
**SE:** Delivery is a pure function: inputs in, Outcome out. The probability model lives in domain.delivery as a pure function (ADR-005). Randomness injected via seed, not Math.random(). The model weights: shot suitability against ball characteristics, surface condition modifier, bowler skill vs batsman skill. The Outcome value object includes trajectoryHint (optional) for future rendering (ADR-001). GameRenderer interface receives the Outcome — rendering is a separate concern.

**Cyber:** Value object validation (SEC-003): BallCharacteristics rejects pace/spin outside 0-1. ShotSelection rejects invalid shotType. Outcome requires dismissalType when type is Wicket. All validation at construction time.

**UX:** No UI in this increment — domain logic only. The shot selection UI (two-tier grouping, swipe-to-aim) comes in INCR-005/INCR-006. This increment defines the ShotSelection value object that the UI will populate.

**Ops:** DeliveryBowled, ShotPlayed, OutcomeResolved events logged with structured format. OutcomeCalculation logged at DEBUG level with inputs and probability distribution for gameplay balance tuning.

**DDD:** Delivery aggregate references Bowler (player context), Batsman (player context), FieldPlacement (field context), and SurfaceCondition (pitch context) by data, not by holding their state. This is the cross-context dependency point in the bounded context map.

---
## Deployment validation
1. Run unit tests: Delivery created with valid BallCharacteristics and ShotSelection
2. Run unit tests: Outcome resolution produces valid Outcome for each shot type
3. Run unit tests: Probability model is deterministic (same inputs + seed = same outcome)
4. Run unit tests: Wicket outcomes always include dismissal type
5. Run unit tests: Wide and NoBall produce correct runs and are re-bowled
6. Run PVT assertions: Delivery and Outcome instantiate without error

---
## Lifecycle tracking
| Status | Date | Agent | Notes |
|---|---|---|---|
| proposed | 2026-08-22 | Delivery Lead | Proposed to human after INCR-001 validated. |
| human-approved | 2026-08-22 | Human | Approved. Probability model to include all factors, start simple, iterate. |
| in-progress | 2026-08-23 | Delivery Lead | Dispatched to Feature Owner (tests) and Coder (implementation). |

---
## Feature Owner briefing
**Dispatched:** 2026-08-23
**Briefing file:** `workflow/agent/briefings/INCR-002-feature-owner.md`
**Key focus:** Value object validation, Delivery aggregate creation, Outcome resolution determinism, domain events

---
## Coder briefing
**Dispatched:** 2026-08-23
**Briefing file:** `workflow/agent/briefings/INCR-002-coder.md`
**Key focus:** Delivery aggregate, ProbabilityModel pure function, domain events, PVT assertions

---
## Feature Owner sign-off — 2026-08-23
**Tests written:** happy [18] / unhappy [10] / edge [16]
**All passing:** no (tests written, awaiting Coder implementation)
**Test files:**
- `app/src/test/java/com/cricketgame/domain/delivery/BallCharacteristicsTest.kt`
- `app/src/test/java/com/cricketgame/domain/delivery/ShotSelectionTest.kt`
- `app/src/test/java/com/cricketgame/domain/delivery/OutcomeTest.kt`
- `app/src/test/java/com/cricketgame/domain/delivery/DeliveryTest.kt`
- `app/src/test/java/com/cricketgame/domain/delivery/ProbabilityModelTest.kt`

**Coverage summary:**
- **Happy path tests (18):** BallCharacteristics creation with all valid values, all 10 ShotTypes, wrist angle as float, all Outcome types (Runs, Wicket, Wide, NoBall, DotBall), all 4 DismissalTypes, Delivery aggregate creation, domain events (DeliveryBowled, ShotPlayed, OutcomeResolved), deterministic probability model, all factors included (BowlerStats, BatsmanStats, SurfaceCondition, Weather, ball age)
- **Unhappy path tests (10):** BallCharacteristics pace/spin outside 0-1, Wicket without dismissalType, negative runs, Runs/Wide/NoBall/DotBall with dismissalType set, negative deliveryNumber, empty deliveryId
- **Edge case tests (16):** Boundary values (pace=0/1, spin=0/1), deliveryNumber 0-5, all Line/Length combinations, all ShotType combinations, seed at 0/Long.MAX_VALUE/-42, zero stats, max degradation, high ball age, Wide/NoBall produce 1 run and are illegal

**Acceptance criteria coverage:**
- [x] Delivery aggregate created with BallCharacteristics and ShotSelection
- [x] 10 shot types available
- [x] Wrist angle as float value
- [x] Outcome resolution with all factors
- [x] Outcome types: Runs (0-6), Wicket, Wide, NoBall, DotBall
- [x] Dismissal types: Bowled, Caught, LBW, Stumped (RunOut excluded)
- [x] Probability model is deterministic given a seed
- [x] Domain events emitted: DeliveryBowled, ShotPlayed, OutcomeResolved
- [ ] PVT assertions (Coder responsibility)

**Ready for:** Coder implementation to make tests pass
