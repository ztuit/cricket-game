# INCR-004 — Match flow — overs, bowler rotation, match end
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** medium
**Dependencies:** INCR-003
**Created:** 2026-08-20

---
## Purpose
Implement the full match flow: over completion triggers bowler rotation, bowlers are selected from the roster respecting the 4-over limit (standard T20 rules), and the match progresses from over to over until completion. This is what makes it a match, not just a sequence of deliveries.

**Ubiquitous language terms involved:**
Over, Bowler, BowlerType, ExperienceClass, Match, InningsProgress, Field Placement

---
## Acceptance criteria
- [ ] Over completes after 6 legal deliveries
- [ ] Bowler rotation: new bowler selected for each over from the roster
- [ ] Max 4 overs per bowler enforced (standard T20 rules, A-4)
- [ ] Bowler selection considers overs remaining — if a bowler has bowled 4, they cannot bowl more
- [ ] Field Placement changes between overs (selected based on new bowler's BowlerType)
- [ ] Domain events emitted: BowlerChanged, FieldPlacementChanged
- [ ] Over break: summary of over (runs, wickets) available as data
- [ ] Match end: result calculated and MatchCompleted event emitted

---
## Technical notes
**SE:** Bowler rotation is a Match aggregate responsibility. The Match holds the bowler roster and tracks overs bowled per bowler. Selection algorithm: pick the bowler with the fewest overs bowled who is eligible (has not reached 4 overs). If multiple bowlers are tied, pick randomly (seeded). Field Placement is selected from predefined sets based on the new bowler's BowlerType (ADR-006).

**Cyber:** None.

**UX:** No UI in this increment. The over break screen and bowler announcement come later. This increment provides the data (new bowler, over summary) that the UI will display.

**Ops:** BowlerChanged event logged with matchId, newBowlerId, bowlerType. OverCompleted event logged with matchId, overNumber, runsThisOver, wicketsThisOver.

**DDD:** Match aggregate owns the bowler roster and tracks overs per bowler. Bowler entity enforces the 4-over invariant. Field Placement is referenced by BowlerType — the relationship defined in the bounded context map.

---
## Deployment validation
1. Run unit tests: over completes after 6 legal deliveries
2. Run unit tests: bowler changes at over boundary
3. Run unit tests: bowler cannot bowl more than 4 overs
4. Run unit tests: bowler rotation selects eligible bowler
5. Run unit tests: field placement changes with bowler type
6. Run unit tests: match ends correctly after 20 overs with correct result
7. Run PVT assertions: Bowler enforces 4-over invariant
