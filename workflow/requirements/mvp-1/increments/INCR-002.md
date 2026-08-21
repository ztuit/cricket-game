# INCR-002 — Delivery loop — ball delivery, batting input, outcome resolution
**MVP:** 1
**Status:** open
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
