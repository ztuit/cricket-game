# INCR-008 — Surface physics model and weather effects
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** high
**Dependencies:** INCR-002, INCR-006
**Created:** 2026-08-20

---
## Purpose
Implement the surface physics model: pitch zones degrade independently over the match, weather affects degradation rate, and ball age affects swing. Surface condition influences delivery outcomes through the probability model. This is the game's deepest feature — the one that rewards cricket knowledge.

**Ubiquitous language terms involved:**
Pitch, Surface Condition, Weather, Delivery, BallCharacteristics, Outcome

---
## Acceptance criteria
- [ ] Pitch divided into zones, each with independent SurfaceCondition (degradation, moisture, roughness)
- [ ] Degradation is monotonically non-decreasing (SurfaceCondition invariant from ddd.md)
- [ ] Degradation increases based on: number of deliveries to that zone, weather condition
- [ ] Weather affects degradation rate: Sunny increases degradation, Overcast/Humid slows it
- [ ] Ball age (deliveries bowled) affects swing potential
- [ ] Surface condition modifies delivery outcome probability in the resolution model
- [ ] Degraded zones produce more variable bounce and more turn (higher chance of unusual outcomes)
- [ ] Domain event emitted: SurfaceConditionChanged when a zone degrades
- [ ] PVT assertions: SurfaceCondition enforces monotonic degradation, Weather values in valid range

---
## Technical notes
**SE:** Pitch aggregate owns a grid of SurfaceCondition value objects. Degradation is calculated per delivery: base amount + weather modifier + zone-specific factors. The probability model in domain.delivery reads SurfaceCondition and adjusts outcome probabilities. This is pure computation — no I/O. The model must be deterministic given a seed.

**Cyber:** None — pure computation.

**UX:** Surface condition displayed as colour-coded zones (INCR-006). This increment implements the physics behind the colours. The UI shows "The pitch is starting to crack on a good length" as flavour text at over breaks. Player sees the effect (colour change) without needing to understand the model.

**Ops:** SurfaceConditionChanged event logged at DEBUG level with matchId, zoneId, newCondition. OutcomeCalculation logged with surface condition inputs for debugging.

**DDD:** Pitch aggregate with SurfaceCondition value objects per zone. Weather is a match-level constant. SurfaceCondition references are passed to the Delivery resolution function. Monotonic degradation invariant enforced at the value object level.

---
## Deployment validation
1. Run unit tests: SurfaceCondition degradation increases after deliveries
2. Run unit tests: degradation is monotonic (cannot decrease)
3. Run unit tests: weather modifies degradation rate correctly
4. Run unit tests: degraded zones produce more variable outcomes in probability model
5. Run unit tests: ball age affects delivery characteristics
6. Run PVT assertions: SurfaceCondition and Weather enforces invariants
7. Verify SurfaceConditionChanged event emitted when zone degrades
