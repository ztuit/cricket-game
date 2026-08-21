# INCR-010 — End-to-end match playtest and structured logging
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** medium
**Dependencies:** INCR-004, INCR-009
**Created:** 2026-08-20

---
## Purpose
Play a complete T20 match end-to-end on a physical device. Validate the full core loop works: toss, deliveries, scoring, overs, bowler rotation, match end, result. Implement comprehensive structured logging for all domain events. This is the MVP 1 validation increment — if this works, the core loop is proven.

**Ubiquitous language terms involved:**
Match, Delivery, Over, Bowler, Outcome, InningsProgress, Target, Toss, Result

---
## Acceptance criteria
- [ ] Complete T20 match playable from toss to result on a physical device
- [ ] All domain events logged with structured format: event name, matchId, deliveryId, timestamp
- [ ] MatchStarted, TossCompleted, DeliveryBowled, ShotPlayed, OutcomeResolved, WicketFallen, BoundaryScored, OverCompleted, BowlerChanged, FieldPlacementChanged, InningsCompleted, MatchCompleted events all emitted and logged
- [ ] SurfaceConditionChanged logged at DEBUG level
- [ ] OutcomeCalculation logged at DEBUG level with inputs and probability distribution
- [ ] Match saved after every delivery (persistence from INCR-009 working)
- [ ] App kill and resume works mid-match
- [ ] Match result is correct at end of innings
- [ ] No crashes during a full 20-over match

---
## Technical notes
**SE:** This is an integration increment — all previous domain increments come together. The full match flow: Home -> Pre-Match (toss) -> Delivery Loop (20 overs) -> Result. All domain events wired to structured logging. This is where we verify the architecture holds end-to-end.

**Cyber:** Verify no PII in any log entry (SEC-004). Verify all log entries use matchId as correlation, not device identifiers.

**UX:** Minimal UI for this increment — functional screens sufficient. The visual style (INCR-005) and detailed UI (INCR-006, INCR-007) enhance this but are not required for the playtest. This increment validates the core loop works before polishing the presentation.

**Ops:** All domain events logged per the observability requirements in ops.md. This is the first increment where the full logging strategy is exercised. Verify Crashlytics captures any crashes with match context.

**DDD:** All bounded contexts (Match, Delivery, Player, Field, Pitch) working together. Cross-context dependencies exercised: Match orchestrates Delivery, Delivery uses Player/Field/Pitch data.

---
## Deployment validation
1. Play a complete T20 match on a physical device from start to finish
2. Verify all 12+ domain events are logged (check logcat for structured entries)
3. Kill the app mid-match, relaunch, verify resume works
4. Verify match result is correct (Win/Loss based on target)
5. Verify no crashes during the full match
6. Verify Crashlytics is receiving events (if crash occurs)
7. Verify logcat shows structured log entries with matchId and deliveryId
