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

## [2026-08-21] PIPELINE-001 deployment sign-off — first CI pipeline operational
**Type:** lesson
**Context:** Deployment signoff mode for PIPELINE-001. First increment to go through the full pipeline verification process.
**What happened:** CI pipeline verified successfully. GitHub Actions run at https://github.com/ztuit/cricket-game/actions/runs/32456620183 shows all stages passing: checkout → JDK 17 → Gradle cache → gitleaks → build → test. Commit SHA `6eb0cd0` confirmed in workflow run and embedded in artifact name (`build-outputs-6eb0cd0`). PVT assertions (`BuildConfigurationPvtTest`) run as unit tests within `./gradlew test` — no separate step needed. Two minor deprecation warnings (Node.js 20, setup-java v4) are non-blocking.
**Impact:** The CI foundation is now in place. All subsequent increments can rely on automated build+test on every push. PVT assertions are confirmed working as plain JUnit tests — the Android adaptation of the PVT mechanism is validated. Artifact provenance is traceable: commit SHA → workflow run → uploaded artifact.
**Status:** resolved

## [2026-08-21] PIPELINE-002 deployment sign-off — APK workflow operational
**Type:** lesson
**Context:** Deployment signoff mode for PIPELINE-002. Second pipeline increment — APK build via `workflow_dispatch`.
**What happened:** APK build workflow verified via GitHub API. Run 32485474639 confirmed: `Build APK` workflow, event `workflow_dispatch`, conclusion `success`, artifact `cricket-game-debug-2187f4e` (2.6MB). CI workflow (run 32485422072) also passed on same commit — PIPELINE-001 intact. Artifact named with commit SHA, 30-day retention. No PVT assertions needed (pure infrastructure, no domain objects or runtime service).
**Impact:** Two workflows now operational: `CI` (push trigger, build+test) and `Build APK` (manual trigger, artifact production). The APK artifact can now be downloaded and installed on a device. Next step is PIPELINE-004 (test device deployment via Firebase App Distribution) to automate the install step.
**Status:** resolved

## [2026-08-21] No task tool available for dispatch
**Type:** lesson
**Context:** User requested "use the task tool to dispatch" to Delivery Lead after PE sign-off. The task tool is not available in the PE's toolset.
**What happened:** Created dispatch file at `workflow/requirements/mvp-1/dispatches/PIPELINE-002-dl-validation.md` and provided HANDOFF block. This is the best available mechanism.
**Impact:** If the workflow requires a task tool for agent dispatch, it needs to be added to the PE's toolset. For now, dispatch files + HANDOFF blocks are the workaround.
**Status:** open

## [2026-08-22] INCR-001 deployment sign-off — domain model foundation
**Type:** lesson
**Context:** Deployment signoff mode for INCR-001. First feature increment — core domain objects for cricket game.
**What happened:** Verified commit SHA `4ba1bc3` in git log. CI pipeline structure confirmed (`.github/workflows/ci.yml` — checkout → JDK 17 → Gradle cache → gitleaks → build → test). All 61 tests pass (58 domain + 2 PVT + 1 placeholder). Domain layer has zero Android imports — `grep` for `import android.` / `import androidx.` returned no matches across all 18 domain files. Domain objects implement the DDD model: Match aggregate with invariants, Bowler/Batsman entities, Pitch/Ground/SurfaceCondition, FieldPlacement with 11 fielders, InningsProgress, TossResult, DomainEvents (MatchStarted, TossCompleted). PVT assertions verified via unit tests (no startup hook yet — PIPELINE-004 not deployed).
**Impact:** Domain purity (ADR-002) is verified. The foundation for all subsequent increments is solid. The58 domain tests" claim in the release note is accurate (7+22+16+13=58). One observation: the release note says "local (unit tests pass, domain layer has no Android dependencies)" as the environment — this is correct for an Android app with no server component, but differs from the standard "staging" environment in the PE template.
**Status:** resolved
