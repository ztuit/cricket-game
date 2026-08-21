## [2026-08-20] Initial ops assessment for Cricket Game
**Type:** decision
**Context:** Initial review as Ops Support agent after Senior Engineer produced architecture. Offline Android game, no server in MVP 1-3.
**What happened:** Produced workflow/techsme/ops.md with full operational assessment. Key decisions: structured logging for all domain events (MatchStarted, DeliveryBowled, OutcomeResolved, etc.) with matchId+deliveryId correlation; Firebase Crashlytics as hard dependency; Room save error logging at ERROR level; delivery resolution performance profiling target <200ms. No PII in MVP 1. No remote monitoring — all observability is device-local (logcat + Crashlytics).
**Impact:** MVP 1 blocking requirements established. Coder must implement structured logging and Crashlytics integration from increment 1. PVT assertion must validate Room schema. Runbooks needed for crash investigation and match state issues.
**Status:** resolved
