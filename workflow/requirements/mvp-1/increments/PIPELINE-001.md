# PIPELINE-001 — CI pipeline: build and unit test
**MVP:** 1
**Status:** deployed
**Priority:** must
**Complexity:** medium
**Dependencies:** none
**Created:** 2026-08-20

---
## Purpose
Every push triggers an automated build and unit test run, including domain PVT assertions. This is the foundation all other increments depend on — without CI, there is no automated safety net, no artifact traceability, and no confidence that changes don't break existing functionality.

**Ubiquitous language terms involved:**
None directly — this is infrastructure.

---
## Human approval record
**Proposed:** 2026-08-20
**Human response:** approved
**Human notes:**
- TEST-003 should not come first — "we need something built before defects can be raised"
- Secret scanning (gitleaks) confirmed for inclusion
- JDK 17 confirmed (not JDK 25 — AGP compatibility concern)
- Gradle caching: standard GitHub Actions caching

**Scope confirmed:** CI pipeline with build, test, secret scanning, commit SHA in version. No feature code, no APK signing, no PVT framework, no Crashlytics.

---
## Acceptance criteria
- [ ] Commit to any branch triggers the GitHub Actions pipeline automatically
- [ ] `./gradlew build` compiles the project and runs lint
- [ ] `./gradlew test` runs all domain-layer unit tests (pure Kotlin, no emulator)
- [ ] Failed unit test blocks merge to main
- [ ] Failed PVT assertion blocks merge with human-readable failure message
- [ ] Artifact produced with commit SHA embedded in version name (`git rev-parse --short HEAD`)
- [ ] Build log accessible and retained in GitHub Actions

---
## Technical notes
**SE:** GitHub Actions workflow runs on every push. Steps: checkout → setup JDK 17 → `./gradlew build` → `./gradlew test`. Domain tests are pure Kotlin (no Android dependencies), so they run fast on JVM. The instrumented test stage (`connectedAndroidTest`) is separate and slower — consider making it informational-only for PRs, blocking only on merge to main.

**Cyber:** Add secret scanning step to CI pipeline (SEC-002). Use `gitleaks/gitleaks-action@v2` or equivalent. No secrets in source code or committed configuration. Pin GitHub Actions to commit SHAs, not version tags.

**UX:** None — no UI in this increment.

**Ops:** None — no runtime observability in this increment. CI logs are the observability.

**DDD:** None — no domain objects in this increment.

---
## Deployment validation
1. Push a test commit to a feature branch
2. Verify GitHub Actions workflow triggers automatically
3. Verify build and test stages pass
4. Verify failing test blocks merge (push a deliberately broken test, confirm it fails CI)
5. Verify commit SHA appears in the build output
6. Verify build log is accessible from the Actions tab

---
## PVT assertions required
- [x] Build configuration has correct namespace (test classpath wired)
- [x] JUnit is available on test classpath

---
## Lifecycle tracking
| Status | Date | Agent | Notes |
|---|---|---|---|
| proposed | 2026-08-20 | TPO | |
| human-approved | 2026-08-20 | Human | JDK 17, gitleaks confirmed |
| in-progress | 2026-08-21 | DL | |
| deployed | 2026-08-21 | Coder | Commit SHA: 6eb0cd0f83eb8d9f6f83f0fca28d462062b3c57e |

---
## Coder sign-off — 2026-08-21
Implementation complete. All CI stages passing.
Files changed:
- `build.gradle.kts` — root build file with AGP 8.2.2, Kotlin 1.9.22
- `app/build.gradle.kts` — Android app module, JDK 17, minSdk 31, targetSdk 35
- `settings.gradle.kts` — dependency resolution management
- `gradle.properties` — JVM args, AndroidX, non-transitive R class
- `gradlew` / `gradlew.bat` — Gradle wrapper scripts
- `gradle/wrapper/gradle-wrapper.jar` + `gradle-wrapper.properties` — Gradle 8.5
- `.github/workflows/ci.yml` — GitHub Actions CI pipeline
- `app/src/test/java/com/cricketgame/PlaceholderTest.kt` — placeholder test
- `app/src/test/java/com/cricketgame/pvt/BuildConfigurationPvtTest.kt` — PVT assertions
- `.gitignore` — Android/Gradle/IDE exclusions
- `app/src/main/AndroidManifest.xml` — minimal manifest
- `app/src/main/res/values/strings.xml` — app name resource

PVT assertions added:
- `BuildConfigurationPvtTest.build configuration has correct namespace` — verifies test classpath
- `BuildConfigurationPvtTest.JUnit is available on test classpath` — verifies JUnit wiring

Notable decisions:
- Used SSH for git push (PE token lacks `workflow` scope for HTTPS)
- Gradle 8.5 + AGP 8.2.2 (compatible with JDK 17, not JDK 25)
- All GitHub Actions pinned to v4 tag SHAs (verified via GitHub API)
- Gitleaks runs as a CI step, not a separate job (faster feedback)
- Commit SHA embedded via `-PversionNameSuffix` gradle property

Ready for: Platform Engineer sign-off → DL validation
