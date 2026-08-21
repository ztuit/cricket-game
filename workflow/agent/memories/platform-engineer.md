# Platform Engineer Memory

## [2026-08-20] Initial review — PVT adaptation for Android
**Type:** decision
**Context:** Platform Engineer initial_review mode. Cricket Game is an Android app (Kotlin, Jetpack Compose), offline-first, no server component in MVP 1–3.
**What happened:** The PVT mechanism was designed for server-side services where "service start" is binary. For Android, I adapted it to a two-tier model: (1) Domain PVT assertions run as pure Kotlin unit tests in CI — fast, every push, verify domain object instantiation, value object invariants, static data loading; (2) App launch verification on device — the "service started" equivalent, confirmed by app launching without crash.
**Impact:** PVT assertions can be written by the Coder as regular unit tests (no special framework needed). The domain purity decision from the SE makes this straightforward — domain layer has zero Android dependencies, so PVT assertions are plain JUnit tests. This is significantly simpler than the server-side PVT hook pattern described in the agent template.
**Status:** resolved

## [2026-08-20] Pipeline design — no automated deployment target
**Type:** decision
**Context:** Android app has no server to deploy to. "Deployment" = "install APK on device."
**What happened:** Pipeline design adapted: CI produces a signed APK/AAB artifact. Deploy step is manual (ADB install or Firebase App Distribution). The pipeline gates are: build passes, tests pass (including PVT), artifact produced. The "service started" check happens after manual install — either the human confirms launch success, or Crashlytics reports a crash.
**Impact:** Deployment sign-off requires two evidence sources: (1) CI workflow logs showing build+test passed, and (2) confirmation that the app launched on device without crash. This is the mobile equivalent of checking GitHub Actions logs AND the running service on Railway.
**Status:** resolved

## [2026-08-20] CI performance concern — instrumented tests
**Type:** open-question
**Context:** SE's pipeline includes `connectedAndroidTest` (instrumented tests on emulator). These are slow on GitHub Actions (2–5 min).
**What happened:** Raised as feedback to SE: consider making instrumented tests optional for PRs (run on merge to main only). Domain tests + PVT assertions (< 5 seconds) should be the fast gate.
**Impact:** If instrumented tests are required on every PR, developer feedback loop is slow. Need SE or TPO to decide.
**Status:** open
