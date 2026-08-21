# PIPELINE-005 — Crashlytics and crash alerting
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** low
**Dependencies:** PIPELINE-004
**Created:** 2026-08-20

---
## Purpose
Crashes on test devices are captured and reported with match context. Email alerts configured for new crash types. Crash-free rate visible on Firebase dashboard. This completes the observability foundation for MVP 1.

**Ubiquitous language terms involved:**
None directly — this is infrastructure.

---
## Acceptance criteria
- [ ] Crashlytics SDK integrated in both debug and release builds
- [ ] Custom keys set on match start: matchId, groundId, weather
- [ ] Crash appears in Firebase console within 5 minutes of occurrence
- [ ] Email alert configured for new crash types
- [ ] Crash-free rate visible on Firebase dashboard

---
## Technical notes
**SE:** Crashlytics is already added in PIPELINE-004. This increment ensures custom keys are wired into the match lifecycle (set on MatchStarted event) and that alerting is configured in the Firebase console. Debug builds should use Crashlytics debug mode for testing.

**Cyber:** No additional security concerns. Crashlytics collects device info — privacy policy deferred to MVP 2 per Cyber assessment.

**UX:** None.

**Ops:** Custom keys (matchId, groundId, weather) provide crash context without PII. All alerts notify the solo developer via email. No on-call rotation needed.

**DDD:** None.

---
## Deployment validation
1. Start a match on test device (triggers MatchStarted and sets custom keys)
2. Trigger a deliberate crash mid-match
3. Verify crash appears in Firebase console with matchId, groundId, weather custom keys
4. Verify email alert received for the new crash type
5. Verify crash-free rate metric is visible on the Firebase dashboard
