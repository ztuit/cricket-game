## Increment Proposal — INCR-002: Delivery loop — ball delivery, batting input, outcome resolution

**MVP:** 1
**What:** The core gameplay loop — a ball is bowled, the player selects a shot, and the outcome is resolved by a probability model. This is the atomic unit of gameplay that everything else orbits around.
**Why now:** INCR-001 proved the domain model works. INCR-002 builds the actual gameplay mechanic on top of it. Without this, there is no game — just a match that never starts.
**Scope — included:**
- Delivery aggregate with BallCharacteristics (line, length, pace, spin)
- ShotSelection value object (10 shot types + wrist angle float)
- Outcome resolution via deterministic probability model (seed-based, ADR-005)
- Outcome types: Runs (0-6), Wicket (with dismissal type), Wide, NoBall, DotBall
- Dismissal types: Bowled, Caught, LBW, Stumped (RunOut excluded for single batsman model)
- Domain events: DeliveryBowled, ShotPlayed, OutcomeResolved
- PVT assertions for Delivery and Outcome instantiation

**Scope — not included:**
- Shot selection UI (two-tier grouping, swipe-to-aim) — that's INCR-005/INCR-006
- GameRenderer integration — rendering is a separate concern
- TrajectoryHint rendering — value object exists but rendering deferred
- RunOut dismissal — excluded for single batsman model (MVP 1)

**Acceptance criteria summary:**
- [ ] Delivery aggregate created with BallCharacteristics and ShotSelection
- [ ] 10 shot types available (Drive, Pull, Cut, Sweep, Defensive, Leave, LegGlance, Slog, ReverseSweep, UpperCut)
- [ ] Wrist angle as float value
- [ ] Outcome resolution: probability model takes (BallCharacteristics, ShotSelection, BowlerStats, BatsmanStats, SurfaceCondition) → Outcome
- [ ] Outcome types: Runs (0-6), Wicket (with dismissal type), Wide, NoBall, DotBall
- [ ] Dismissal types: Bowled, Caught, LBW, Stumped
- [ ] Probability model is deterministic given a seed (ADR-005)
- [ ] Domain events emitted: DeliveryBowled, ShotPlayed, OutcomeResolved
- [ ] PVT assertions: Delivery can be created, Outcome enforces invariants

**Deployment validation:** Unit tests verify Delivery creation, Outcome resolution for all shot types, deterministic probability model, wicket outcomes include dismissal type, Wide/NoBall produce correct runs.

**Complexity:** high
**Dependencies:** INCR-001 (done)

---

Questions before we begin:
1. Is this the right increment to build next, or should it be deferred?
2. Is the scope as you understand it?
3. Are there edge cases or constraints not captured here?
4. Is there anything about this increment that should change?
