# PIPELINE-003 — PVT Assertion Framework
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** medium
**Dependencies:** PIPELINE-001, INCR-001
**Created:** 2026-08-20

---
## Purpose
Establishes the PVT (Production Verification Test) mechanism — runtime assertions that run at application startup. If any PVT assertion fails, the app refuses to start. This is the safety net that proves each deployed feature is correctly wired in the live environment.

**PVT is NOT:**
- Unit tests (those run in CI via `./gradlew test`)
- Integration tests (those verify feature behaviour)
- Deployment-time checks (those are PE's sign-off)

**PVT IS:**
- Runtime assertions in the app itself
- Runs at startup, before the app accepts user interaction
- Aware of the live running context (environment, dependencies, configuration)
- Causes the app to NOT start if an assertion fails
- Produces a health report confirming all PVTs passed

---
## Acceptance criteria
- [ ] PVT assertions run at application startup (before UI renders)
- [ ] Failed assertion prevents the app from starting — clear error screen/message shown
- [ ] PVT assertions are aware of live context (not just class existence — real environment checks)
- [ ] Health endpoint/report confirms all PVTs passed (accessible for PE verification)
- [ ] Coder documentation: how to register a new PVT assertion
- [ ] At least one example PVT assertion (e.g., domain objects instantiate, database reachable)
- [ ] PVT assertions complete in under 5 seconds total
- [ ] Hook pattern approved by SE

---
## Technical notes
**SE:** PVT assertions run in the Android Application class or early Activity lifecycle, before the UI renders. Failed PVTs show an error screen explaining what failed, not a crash. The health report is a simple in-memory summary that could later be exposed via a debug endpoint or log output. Each new increment adds its own PVT assertions — the framework grows with the codebase.

**Cyber:** PVT assertions should not expose sensitive information in error messages. Health report should not be accessible to end users in production (debug builds only, or behind a flag).

**UX:** PVT failure screen should be clear and informative for the developer, not alarming for a test user. Simple text explaining what failed.

**Ops:** PVT health report should be loggable for PE verification. Structured log output preferred.

**DDD:** PVT assertions should verify domain object invariants and aggregate integrity — the same invariants defined in `ddd.md`.

---
## Deployment validation
1. Add a deliberately broken PVT assertion
2. Launch the app — verify it does NOT start and shows a clear error
3. Fix the assertion — verify the app starts normally
4. Verify health report shows all PVTs passed
5. Read the Coder documentation — confirm a new developer could add an assertion independently
6. Verify timing is under 5 seconds
