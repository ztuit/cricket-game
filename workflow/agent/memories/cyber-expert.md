# Cyber Expert — Memory

## [2026-08-20] Initial security assessment — MVP 1
**Type:** decision
**Context:** Initial review after DDD Expert and Senior Engineer completed. Reviewed ddd.md (all domain objects), senior-engineer.md (Kotlin + Compose + Room, offline-first), and bluesky v1 (cricket strategy game).
**What happened:** Produced cyber.md with threat model, STRIDE analysis, data protection assessment, and security requirements. Finding: MVP 1 has no critical or high security findings blocking approval. The offline, no-PII, no-server architecture eliminates primary attack vectors. One medium finding: Firebase Crashlytics requires privacy policy for Play Store. Two hygiene items: secret scanning in CI, value object validation.
**Impact:** MVP 1 is security-clear. Critical requirements correctly deferred: SEC-006 (IAP server-side validation, MVP 3), SEC-007 (leaderboard anti-cheat, MVP 4), SEC-008 (server-side encryption, MVP 4). These are architectural reservations, not blockers.
**Status:** resolved

## [2026-08-20] Future security gates identified
**Type:** decision
**Context:** Reviewing the product roadmap for future MVPs.
**What happened:** Identified that MVP 3 (IAP) and MVP 4 (leaderboards, accounts) each introduce critical security requirements that MUST be addressed before those MVPs ship. IAP client-only validation is trivially spoofable. Leaderboard scores without server-side validation are meaningless. Player PII requires encryption.
**Impact:** These are hard gates on future MVPs. The architecture already reserves seams (Monetisation interface, repository pattern for remote data sources). Senior Engineer must design the specific validation mechanisms before those increments begin.
**Status:** open — deferred to MVP 3/4 design phases
