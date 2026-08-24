# Delivery Lead Memory

## [2026-08-20] PIPELINE-001 delivery initiated
**Type:** decision
**Context:** First increment of MVP 1. PIPELINE-001 is infrastructure — CI pipeline with build, test, secret scanning. No Feature Owner needed (no functional tests). Coder implements directly.
**What happened:** Read all required inputs (increment file, SE architecture, PE platform strategy, DDD model). Increment is human-approved with clear acceptance criteria. Dispatching to Coder.
**Impact:** This is the foundation all other increments depend on. Must be solid before INCR-001 (domain layer) begins.
**Status:** open

## [2026-08-21] PIPELINE-001 validated and done
**Type:** decision
**Context:** PE signed off on PIPELINE-001 deployment. DL validation required before marking done.
**What happened:** Verified all 7 acceptance criteria against deployed CI (GitHub Actions run #3, commit 6eb0cd0). All criteria met: pipeline triggers on push to any branch, build and test stages pass, PVT assertions run as JUnit tests within `./gradlew test`, artifact produced with commit SHA in version name (`build-outputs-6eb0cd0`), build logs accessible and retained. Two non-blocking deprecation warnings (Node.js 20, setup-java v4) noted.
**Impact:** CI foundation is solid. PIPELINE-002 (signed APK) and PIPELINE-003 (PVT framework) are now unblocked. PIPELINE-002 is the logical next step as it unblocks PIPELINE-004 (Firebase App Distribution) and enables physical device testing.
**Status:** resolved

## [2026-08-21] PIPELINE-002 dispatched to Coder
**Type:** decision
**Context:** PIPELINE-002 is human-approved. Increment is infrastructure (no functional tests, no Feature Owner needed). Coder implements directly.
**What happened:** Read increment file, SE architecture, existing CI workflow (`ci.yml`), and `build.gradle.kts`. Increment status updated to `in-progress`. Coder briefing created at `workflow/requirements/mvp-1/dispatches/PIPELINE-002-coder.md`. Key requirements: add `workflow_dispatch` trigger, configure debug keystore signing, upload APK artifact with SHA in name, retain previous versions.
**Impact:** This unblocks PIPELINE-004 (Firebase App Distribution) and enables physical device testing. Must verify PIPELINE-001 behaviour (build+test on push) remains unchanged.
**Status:** resolved

## [2026-08-21] INCR-001 dispatched — first functional increment
**Type:** decision
**Context:** INCR-001 is human-approved. PIPELINE-001 (dependency) is done. This is the first functional increment — domain model and match creation with toss. Requires both Feature Owner (tests) and Coder (implementation).
**What happened:** Dispatched INCR-001. Feature Owner briefing at `workflow/requirements/mvp-1/dispatches/INCR-001-feature-owner.md` — covers all 10 acceptance criteria with happy/unhappy/edge test categories, ubiquitous language enforcement, toss mechanic detail. Coder briefing at `workflow/requirements/mvp-1/dispatches/INCR-001-coder.md` — covers all domain objects to implement, value objects, enums, match creation flow, technical constraints (pure Kotlin, ADR-002/004/008, SEC-003). Note: PIPELINE-003 (PVT framework) not yet available — PVT acceptance criterion will be verified by unit tests initially, formal PVT assertions added retroactively when PIPELINE-003 lands.
**Impact:** This is the foundation every other functional increment builds on. Domain model must be solid before INCR-002 (delivery loop). Feature Owner writes tests first, Coder implements to pass them — iterative pattern.
**Status:** resolved

## [2026-08-22] INCR-001 validated and done — domain model foundation complete
**Type:** decision
**Context:** INCR-001 deployed (commit 4ba1bc3), PE signed off. DL validation required before marking done.
**What happened:** Verified all 10 acceptance criteria against code and tests. All 58 domain tests pass (25 happy, 16 unhappy, 7 edge). Match aggregate enforces max 20 overs, max 10 wickets. BowlerRoster validates ≥5 bowlers covering all 4 BowlerTypes. Toss mechanic works (random winner, AI chooses if player loses). Target null when batting first, positive integer when chasing. Ground/Pitch/FieldPlacement/InningsProgress all correctly implemented. Domain events (MatchStarted, TossCompleted) emitted. PVT assertions verified via unit tests. Ubiquitous language from ddd.md used consistently. Increment marked done.
**Impact:** Domain model foundation is solid. INCR-002 (delivery loop) is now unblocked — this is the core gameplay mechanic. Also unblocks INCR-007 (bowler character system) and TEST-001 (test framework).
**Status:** resolved
