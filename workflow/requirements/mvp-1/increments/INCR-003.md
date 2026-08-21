# INCR-003 — Scoring and match state tracking
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** medium
**Dependencies:** INCR-002
**Created:** 2026-08-20

---
## Purpose
Track the running score, wickets fallen, and overs completed. Update InningsProgress after each delivery. Determine when the innings is complete (all overs, all wickets, or target reached). This gives the player feedback on how they are doing.

**Ubiquitous language terms involved:**
Runs, Wicket, Boundary, InningsProgress, Over, Delivery, Target, Outcome

---
## Acceptance criteria
- [ ] InningsProgress updated after each delivery: runs scored, wickets fallen, balls this over
- [ ] Boundaries detected: 4 runs (ball along ground to boundary), 6 runs (ball in air to boundary)
- [ ] Wide and NoBall: 1 run added, ball is re-bowled (does not count as a legal delivery)
- [ ] Over completion: 6 legal deliveries complete an over
- [ ] Innings end conditions: all 20 overs bowled, all 10 wickets fallen, or target reached
- [ ] Target reached: player wins when score exceeds target
- [ ] Domain events emitted: BoundaryScored (for 4s and 6s), WicketFallen, OverCompleted
- [ ] Match result determined: Win, Loss, or Draw (tie if scores equal after 20 overs)

---
## Technical notes
**SE:** InningsProgress is a value object updated immutably after each delivery. The Match aggregate owns InningsProgress and enforces invariants (wickets <= 10, balls <= 6 per over). Match end logic is a pure function: given current InningsProgress and target, is the match over? Domain events emitted via Flows.

**Cyber:** None — scoring is pure computation with no external inputs.

**UX:** No UI in this increment — domain logic only. The scoreboard display comes later. InningsProgress is the data source for the scoreboard.

**Ops:** InningsCompleted and MatchCompleted events logged with structured format. BoundaryScored and WicketFallen events logged for player satisfaction signal tracking.

**DDD:** InningsProgress is a value object within the Match aggregate. It references Delivery outcomes but does not own them. The Match aggregate enforces all match-level invariants.

---
## Deployment validation
1. Run unit tests: InningsProgress updates correctly after a single delivery
2. Run unit tests: Boundary detection works (4 and 6 runs)
3. Run unit tests: Wide and NoBall add 1 run and do not count as legal delivery
4. Run unit tests: Over completes after 6 legal deliveries
5. Run unit tests: Innings ends when 20 overs complete, 10 wickets fallen, or target reached
6. Run unit tests: Match result is correct (Win when target exceeded, Loss when innings ends short)
7. Run PVT assertions: InningsProgress enforces invariants
