# INCR-009 — Match persistence and resume
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** medium
**Dependencies:** INCR-004
**Created:** 2026-08-20

---
## Purpose
Persist match state to Room (SQLite) after every delivery so the player can resume a match after app kill or crash. This is the recoverability NFR from the architecture — the game must not lose progress.

**Ubiquitous language terms involved:**
Match, InningsProgress, Delivery, Over, Bowler

---
## Acceptance criteria
- [ ] Match state persisted to Room database after every delivery
- [ ] Room schema maps to Match aggregate (match, overs, deliveries, bowler roster)
- [ ] App kill mid-match resumes from last saved delivery with correct InningsProgress
- [ ] Match resume verifies state consistency (wickets <= 10, balls <= 6, score >= 0)
- [ ] Room save errors logged at ERROR level with match context
- [ ] PVT assertion: Room schema matches domain model expectations on startup
- [ ] Match serialization round-trip test: serialize mid-match state, deserialize, verify invariants hold

---
## Technical notes
**SE:** Room entities mirror the Match aggregate structure. Repository pattern (ADR-003) provides the interface: MatchRepository with save(Match) and load(matchId): Match. Room implementation maps domain objects to Room entities and back. Auto-save triggers after each delivery outcome is resolved. Match resume logic on app start: check for incomplete match, load it, verify state.

**Cyber:** Room database stores non-sensitive game state. No encryption needed for MVP 1 (Cyber assessment). No PII stored.

**UX:** No UI in this increment — persistence is invisible to the player. The resume behaviour is seamless: app opens, match continues from where it left off.

**Ops:** Room save success/failure logged with structured format (matchId, deliveryId, success). Room save errors logged at ERROR level. DataLayerError event for any Room exceptions.

**DDD:** Match aggregate must be serializable to Room entities and back. The serialization must preserve all invariants. InningsProgress is embedded in the Match entity. Delivery and Over are child entities with foreign keys to Match.

---
## Deployment validation
1. Start a match, play 5 deliveries
2. Kill the app (force stop)
3. Relaunch — verify match resumes from delivery 5 with correct score, wickets, overs
4. Run unit test: serialization round-trip preserves all Match invariants
5. Run PVT assertion: Room schema matches domain model
6. Verify Room save errors are logged at ERROR level (test by corrupting a Room write if possible)
