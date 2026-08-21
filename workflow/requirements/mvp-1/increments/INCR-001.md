# INCR-001 — Domain model and match creation with toss
**MVP:** 1
**Status:** open
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
