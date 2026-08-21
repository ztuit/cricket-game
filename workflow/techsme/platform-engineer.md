# Platform Engineering Strategy — Cricket Game
**Status:** draft

---

## Full Technical Detail

### Environment strategy

| Environment | Purpose | Who deploys | Promotion trigger |
|---|---|---|---|
| local | Developer testing on emulator or physical device | Coder | manual — run from Android Studio |
| ci | Automated build, lint, unit tests, instrumented tests | Pipeline (GitHub Actions) | every push |
| test-device | Manual gameplay testing on physical hardware | Coder (ADB or Firebase App Distribution) | merge to main |
| production-like | Pre-release validation via Google Play internal testing track | Coder (signed AAB upload) | manual gate |
| production | Live on Google Play Store | Pipeline + manual release gate | manual gate |

**MVP 1 note:** The "production-like" environment IS a physical Android device with a signed APK installed. There is no server. CI produces the artifact; the human installs it and plays. This is fundamentally different from a web service — deployment is "install APK on device," not "push to a server."

### Pipeline design

| Stage | Trigger | Steps | Gate |
|---|---|---|---|
| Build | Every push | `./gradlew build` — compile, lint | Build must pass |
| Unit test | Every push | `./gradlew test` — domain layer unit tests (pure Kotlin, fast, no emulator) | All tests pass |
| Android test | Every push | `./gradlew connectedAndroidTest` — instrumented tests on emulator | All tests pass |
| PVT check | Every push | Domain PVT assertions run as unit tests (pure Kotlin, no Android dependencies) | All assertions pass |
| Package | Merge to main | Produce signed APK/AAB via Gradle signing config | Artifact produced with commit SHA |
| Deploy (test-device) | Manual trigger | Install APK via ADB or push to Firebase App Distribution | App launches without crash |
| Deploy (production-like) | Manual gate | Upload signed AAB to Google Play internal testing track | App installs and launches |

**Key difference from web service:** There is no automated deployment to a running server. The deploy step is "produce an installable artifact" + "human installs it on a device." The PVT check happens at two levels: (1) domain assertions run as unit tests in CI (fast, every push), and (2) app launch verification on device (manual, after install).

### Artifact standards

| Concern | Decision | Rationale |
|---|---|---|
| Artifact type | Signed APK (debug for test-device) / Signed AAB (release for production-like) | APK for direct install; AAB for Google Play (required since 2021) |
| Versioning | Commit SHA embedded in version name + auto-incrementing version code | Traceability from artifact to source. `git rev-parse --short HEAD` in `versionName` |
| Registry | GitHub Actions artifact storage (APK/AAB attached to workflow run) | Free, traceable, retained per retention policy. Firebase App Distribution for test device distribution. |
| Immutability | Once built, artifact is not modified. Rebuild produces new artifact with new SHA. | Standard practice. Signed artifacts cannot be tampered with without invalidating signature. |
| Provenance | GitHub Actions workflow run links artifact to commit SHA, branch, and triggering actor | Full audit trail from code change to installable artifact |

### Infrastructure as code

- **Tool:** Gradle build scripts (Kotlin DSL) — the Android equivalent of IaC for build/deploy
- **Location:** `build.gradle.kts` at project root and module level
- **Environment parity:** Debug vs Release build types. Debug enables logging, test hooks, and crashlytics debug mode. Release strips these. Both run the same domain code.
- **Drift detection:** `./gradlew dependencies` — dependency tree is declared, not manually managed. Renovate or Dependabot for automated dependency updates.

**Note:** There is no server infrastructure to provision in MVP 1–3. When MVP 4 introduces server components, Terraform/Pulumi will be introduced for server IaC. The build pipeline itself (GitHub Actions workflows) IS the infrastructure-as-code for CI/CD.

### PVT startup hook

**The adaptation problem:** PVT was designed for server-side services where "service start" is a binary observable event. For an Android app, the equivalent is "app launches without crash and domain objects can be instantiated." The mechanism must be adapted.

**PVT strategy for Android (two-tier):**

**Tier 1 — Domain PVT assertions (CI, every push, fast):**
Pure Kotlin assertions that run as unit tests. No Android dependencies. These verify:
- Domain objects can be instantiated without error
- Value object invariants hold (e.g., `SurfaceCondition` values in valid range)
- Aggregates enforce their rules (e.g., `Match` rejects > 20 overs)
- Enum values are complete and consistent
- Static data (grounds, field placements) loads and validates

These run in `./gradlew test` alongside Feature Owner tests. They are fast (milliseconds) and catch configuration/data issues before the app ever reaches a device.

**Tier 2 — App launch PVT (device/emulator, after install):**
Instrumented test or manual verification that:
- `Application.onCreate()` completes without crash
- Room database initializes and migrations (if any) succeed
- Static data loads into memory
- First `Match` aggregate can be created (domain objects wired correctly)
- No crash on opening the main Activity

This is the "service started" equivalent. If the app launches and displays the initial screen without crashing, PVT passed.

**PE responsibilities:**
- Define the PVT assertion pattern: where assertions live, how they're registered, how failures are reported
- Ensure domain PVT assertions are wired into the CI test stage (they run as unit tests)
- Ensure app launch PVT is part of the deploy validation checklist
- Document the pattern so Coders know how to register new assertions per increment
- Ensure failed PVT produces clear log output (unit test failure messages for Tier 1, logcat for Tier 2)

**PVT assertions (written by Coder) cover:**
- Expected code and configuration is present and wired
- Static data files load without error
- Domain objects can be instantiated without error
- Value object invariants hold
- Room database schema matches domain model expectations
- Security configuration is in place (when relevant)

**PVT hook location:** `app/src/test/java/.../pvt/` for domain assertions (Tier 1). `app/src/androidTest/java/.../pvt/` for launch assertions (Tier 2).
**Expected CI time addition:** Under 5 seconds for domain PVT assertions (pure Kotlin). Under 30 seconds for instrumented launch test on emulator.

### Observability platform

**MVP 1 observability is minimal by design — there is no server to observe.**

| Concern | Tool | Configuration location |
|---|---|---|
| Log aggregation | Android `Logcat` + Firebase Crashlytics (crash logs) | `app/build.gradle.kts` (Crashlytics plugin), `Log` calls in domain/UI code |
| Metrics | None for MVP 1. Future: match completion rate, average score, shot selection distribution. | N/A |
| Tracing | None for MVP 1. | N/A |
| Alerting | Firebase Crashlytics crash alerts (email to developer) | Firebase console configuration |
| Dashboards | Firebase Crashlytics dashboard (crash-free rate, crash clusters) | Firebase console |

**Logging standards:**
- Every domain event logged with: event name, match ID, delivery ID (if applicable), timestamp
- Game engine decisions logged at DEBUG level: probability calculations, surface condition changes
- Errors logged with full context: what was the match state when the error occurred
- Crashlytics custom keys: matchId, overNumber, deliveryNumber (for crash context)

### Migration strategy

**MVP 1:** No migration needed — fresh install, no existing data.

**Future (MVP 2+):** Room database migrations. Strategy:
- Room's built-in migration mechanism (`Migration` objects)
- Every schema change includes a migration path — no destructive migrations without explicit human approval
- Migration PVT assertion: verify database version matches expected after upgrade
- Test migrations in CI using Room's migration testing helper

**Future (MVP 4):** Server-side data migration. Strategy TBD when server infrastructure is designed.

### Rollback strategy

**MVP 1 (APK on device):**
1. Uninstall current version
2. Install previous version APK (available from GitHub Actions artifact storage)
3. Note: Room database may not be backward-compatible — if schema changed, match history is lost on rollback

**Future (Google Play):**
1. Halt staged rollout
2. Upload previous AAB as new release (Google Play does not support true rollback)
3. Or: use Android App Bundle's dynamic delivery to revert specific modules

**Rollback readiness for MVP 1:**
- Previous version APK retained in GitHub Actions artifacts (90-day default retention)
- Rollback = uninstall + reinstall previous APK
- Data loss acceptable for MVP 1 (no user accounts, no cloud saves)

### Platform requirements (blocking)

| Requirement | Priority | From | Acceptance criteria |
|---|---|---|---|
| CI pipeline: build + unit test on every push | critical | MVP 1 | Commit triggers pipeline; failed test blocks merge; artifact produced with commit SHA |
| Domain PVT assertions wired into CI test stage | critical | MVP 1 | PVT assertions run as unit tests; failed assertion blocks merge; failure message human-readable |
| App launch PVT defined and documented | critical | MVP 1 | Coder can add launch assertions; app crash on launch is caught during deploy validation |
| Artifact versioned with commit SHA | critical | MVP 1 | `versionName` contains short SHA; traceable from installed APK to source commit |
| Crashlytics integrated and reporting | high | MVP 1 | Crashes on test device appear in Firebase console within 5 minutes |
| Signed APK produced by CI for merge to main | high | MVP 1 | APK attached to workflow run; installable on physical device |
| Firebase App Distribution configured for test-device deploys | medium | MVP 1 | APK pushed to testers via Firebase; install prompt received |
| Previous version artifact retained for rollback | medium | MVP 1 | Previous APK available in GitHub Actions artifacts |

### Feedback to Senior Engineer

**No architectural concerns.** The SE's architecture is clean for a mobile offline-first app. The domain-purity decision (pure Kotlin, no Android dependencies in domain layer) makes PVT assertions straightforward — they run as plain unit tests. The repository pattern provides clean seams for future server sync.

**One observation:** The SE's CI pipeline includes `connectedAndroidTest` (instrumented tests requiring an emulator). This stage is slow (2–5 minutes on GitHub Actions). Consider making it optional for PRs (run on merge to main only) to keep developer feedback fast. Domain tests + PVT assertions (pure Kotlin, < 5 seconds) should be the fast gate; instrumented tests are the thorough gate.

**MVP 4 impact:** When server infrastructure arrives, the pipeline will need significant expansion: container builds, server deployment stages, database provisioning, and a completely different PVT strategy for server-side services. This is expected and does not affect MVP 1–3 design.

---

## Plain-Language Summary

### Risk
The biggest platform risk is that "deployment" means something very different for a phone app than for a website. There's no server to push to — the game runs entirely on the player's phone. This means we can't automatically deploy and verify in one step. We build the APK in CI, then someone installs it on a phone and checks it works. This manual step is the weakest link, but it's the standard approach for small-team Android development.

### Constraint
No server means no server observability. In MVP 1, if the game crashes on a player's phone, we only know about it if they report it or if Crashlytics catches it. There's no dashboard showing "how many matches are being played" or "what's the average score." This is acceptable for validating the core loop, but it means we're flying blind on player behaviour until MVP 3+ adds analytics.

### Vision impact
The platform strategy supports the full roadmap. MVP 1–3 are purely client-side — the pipeline builds and tests an Android app. MVP 4 introduces server infrastructure, which will require a pipeline expansion (server builds, deployment to cloud, database provisioning). The current design doesn't create any technical debt that blocks MVP 4 — the repository pattern means the domain layer doesn't know or care whether data comes from a local database or a remote server.

### Recommendation
Start with a simple GitHub Actions pipeline: build, test (domain PVT assertions included), produce APK. Don't over-engineer the deploy step — manual APK install is fine for MVP 1. Add Firebase App Distribution when the team grows beyond one person. Add Google Play internal testing track when approaching production quality. The PVT assertions should be written alongside every increment, running as fast unit tests in CI.

---

## PO Approval
**Status:** approved
**Date:** 2026-08-20
**Notes:** Platform strategy is fully compatible with the product vision. The key insight — that "deployment" for a phone app means "install APK on device," not "push to a server" — is correctly reflected in the pipeline design. The two-tier PVT approach (domain assertions as unit tests in CI + app launch verification on device) is pragmatic and appropriate for the offline-first architecture. The five pipeline increments (PIPELINE-001 through PIPELINE-005) are correctly scoped as first-class deliverables. The observation about `connectedAndroidTest` being slow and potentially optional for PRs is a good developer experience consideration. The roadmap alignment is clear: MVP 1–3 pipeline is client-only; MVP 4 introduces server infrastructure — no technical debt created either way.

---

## Pipeline increment definitions

### PIPELINE-001 — CI pipeline: build and unit test
**What:** Every push triggers automated build and unit test run, including domain PVT assertions
**Deployed when:** Test commit triggers successful pipeline run
**Acceptance criteria:**
- [ ] Commit triggers pipeline automatically
- [ ] Failed unit test blocks merge
- [ ] Failed PVT assertion blocks merge with human-readable failure message
- [ ] Artifact produced with commit SHA in version string
- [ ] Build log accessible and retained

### PIPELINE-002 — Signed APK artifact production
**What:** Merge to main produces a signed, installable APK
**Deployed when:** APK produced, attached to workflow run, installable on physical device
**Acceptance criteria:**
- [ ] Merge to main triggers signed APK build
- [ ] APK attached to GitHub Actions workflow run
- [ ] APK versionName contains commit SHA
- [ ] APK installable on physical Android device via ADB
- [ ] Previous version artifact retained in workflow history

### PIPELINE-003 — PVT assertion framework
**What:** Mechanism for Coder to register domain PVT assertions that run in CI
**Deployed when:** Deliberately broken assertion fails CI with clear log; working assertion passes silently
**Acceptance criteria:**
- [ ] PVT assertions run as part of `./gradlew test` (no separate step needed)
- [ ] Failed assertion: test failure with human-readable message naming what is misconfigured
- [ ] Coder documentation written: how to register a new PVT assertion
- [ ] At least one example assertion present (e.g., domain objects instantiate, static data loads)
- [ ] PVT assertions complete in under 5 seconds total
- [ ] Hook pattern approved by SE for architectural consistency

### PIPELINE-004 — Test device deployment via Firebase App Distribution
**What:** APK can be pushed to test devices via Firebase for easy install
**Deployed when:** Test push sends install notification to device; APK installs and launches
**Acceptance criteria:**
- [ ] Firebase project configured with Crashlytics
- [ ] APK pushed to Firebase App Distribution on merge to main
- [ ] Tester receives install notification
- [ ] APK installs and launches on physical device
- [ ] Crashlytics reports crashes from test device within 5 minutes

### PIPELINE-005 — Crashlytics and crash alerting
**What:** Crashes on test devices are captured and reported with context
**Deployed when:** Deliberate crash appears in Firebase console with match context
**Acceptance criteria:**
- [ ] Crashlytics SDK integrated in debug and release builds
- [ ] Custom keys set: matchId, overNumber, deliveryNumber
- [ ] Crash appears in Firebase console within 5 minutes of occurrence
- [ ] Email alert configured for new crash types
- [ ] Crash-free rate visible on Firebase dashboard

---

## Deployment sign-off record (mode: deployment_signoff)

Append to `workflow/requirements/mvp-<N>/releases/<increment-id>-<sha>.md`:

```markdown
## Platform Engineer Deployment Sign-off
**Date:** YYYY-MM-DD
**Increment:** [ID]
**Commit SHA:** [full SHA]
**Environment:** [test-device / production-like / production]
**Pipeline run ref:** [link or ID]

### Pipeline verification
- [ ] Pipeline triggered by correct commit SHA
- [ ] All stages passed (build → test → PVT check → package)
- [ ] Artifact version matches commit SHA in release note
- [ ] No manual steps taken outside pipeline (except device install)

### PVT verification
- [ ] Domain PVT assertions passed in CI (unit test results confirmed)
- [ ] App launched successfully on device without crash — proof Tier 2 PVT passed
- [ ] No crash reports in Crashlytics within 10 minutes of install
- [ ] New PVT assertions added in this increment: [yes — describe / no]

### Observability verification
- [ ] Crashlytics receiving events from test device: [confirmed]
- [ ] Logcat shows expected domain event log entries: [confirmed]
- [ ] No anomalous crash rate post-deployment

### Artifact verification
- [ ] APK/AAB immutable post-build
- [ ] Provenance traceable to CI build (workflow run + commit SHA)
- [ ] Previous version artifact retained for rollback

### Rollback readiness
- [ ] Rollback procedure in release note
- [ ] Previous version APK available in workflow artifacts
- [ ] Rollback tested: yes / no / not required

---
**Status:** pe-deployment-approved | pe-deployment-rejected
**Notes:** [Observations. If rejected: exact reason and what Coder must resolve.]
```

Update increment status: `deployed` → `pe-signed-off` or `pe-rejected`.

## MVP gate sign-off

```markdown
## Platform Engineer MVP Sign-off — MVP [N]
**Date:** YYYY-MM-DD
**Status:** pe-mvp-approved | pe-mvp-rejected
**Pipeline health:**
**PVT coverage growth:**
**Observability:**
**Gaps or concerns:**
**Recommended PVT additions for MVP [N+1]:**
**Notes:**
```
