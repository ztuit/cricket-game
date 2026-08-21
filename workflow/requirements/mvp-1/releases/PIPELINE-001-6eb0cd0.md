# Release Note — PIPELINE-001
**Commit SHA:** 6eb0cd0f83eb8d9f6f83f0fca28d462062b3c57e
**Deployed:** 2026-08-21 07:10 UTC
**Environment:** CI (GitHub Actions)
**MVP:** 1
**Deployed by:** Coder

---
## What was deployed
Initial Android project with CI pipeline. Every push to any branch now triggers an automated build and unit test run, including gitleaks secret scanning. The project uses Kotlin DSL, JDK 17, Gradle 8.5, and AGP 8.2.2. A placeholder test and PVT assertion verify the build configuration is correct.

## PVT assertions added in this increment
| Assertion | What it checks | Failure message |
|---|---|---|
| `BuildConfigurationPvtTest.build configuration has correct namespace` | Test classpath is configured and test source set is wired | PVT FAILURE: Test classpath is not configured. The build configuration may be missing the test source set. |
| `BuildConfigurationPvtTest.JUnit is available on test classpath` | JUnit is on the test classpath and assertions work | PVT FAILURE: JUnit assertions are not working correctly. Check that junit:junit is in testImplementation dependencies. |

**Service started successfully:** yes
_(CI pipeline ran and all stages passed)_

## How to observe it is running
1. GitHub Actions tab shows a green "CI" workflow run: https://github.com/ztuit/cricket-game/actions
2. Commit SHA `6eb0cd0` appears in the build output
3. Build and test artifacts are uploaded to the workflow run

## How to validate it works
1. Push a commit to any branch
2. Verify GitHub Actions workflow triggers automatically
3. Verify all steps pass: checkout, JDK setup, cache, gitleaks, build, test
4. Verify commit SHA appears in the build output
5. Verify build artifacts are uploaded to the workflow run

## How to validate it fails correctly
1. Push a commit with a deliberately broken test (e.g., `assertTrue(false)`)
2. Verify the "Run unit tests" step fails
3. Verify the workflow status shows red/failure
4. Verify the failure message is human-readable in the logs

## Rollback procedure
1. Revert to the previous commit: `git revert HEAD`
2. Push the revert commit
3. CI will run on the reverted code

## Known limitations
- Android instrumented tests (`connectedAndroidTest`) are not included in this increment — they require an emulator and will be added later
- APK signing is not configured — will be added in PIPELINE-002
- Firebase App Distribution is not configured — will be added in PIPELINE-004
- Crashlytics is not integrated — will be added in PIPELINE-005

## Deployment evidence
- GitHub Actions run: https://github.com/ztuit/cricket-game/actions/runs/32456620183
- All 14 steps passed (Set up job, Checkout, JDK 17, Cache, gradlew, SHA, gitleaks, Build, Test, Upload, Post-cache, Post-JDK, Post-checkout, Complete)
- Build time: ~1 minute 40 seconds
