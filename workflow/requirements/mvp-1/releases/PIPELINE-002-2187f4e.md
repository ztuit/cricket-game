# Release Note — PIPELINE-002
**Commit SHA:** 2187f4e94366a293e5f4a2668a0d09f0e15eb8a5
**Deployed:** 2026-08-21 13:10 UTC
**Environment:** GitHub Actions
**MVP:** 1
**Deployed by:** Coder

---
## What was deployed
A separate GitHub Actions workflow (`Build APK`) that produces a signed debug APK on explicit manual trigger (`workflow_dispatch`). The APK is uploaded as a workflow artifact with the commit SHA in the filename. Previous versions are retained for 30 days for rollback.

This workflow is independent of the CI workflow (build + test on every push), which continues to function unchanged.

## How to observe it is running
- GitHub Actions has two workflows: `CI` (push trigger) and `Build APK` (manual trigger)
- `Build APK` workflow has a "Run workflow" button enabled in the GitHub Actions UI
- Manual trigger produces an APK artifact named `cricket-game-debug-<sha>`

## How to validate it works
1. Go to the Actions tab in GitHub: `https://github.com/ztuit/cricket-game/actions`
2. Select the "Build APK" workflow in the left sidebar
3. Click "Run workflow" → select `master` branch → click "Run workflow"
4. Wait for the workflow to complete (approximately 1–2 minutes)
5. Click on the completed workflow run
6. Scroll to "Artifacts" section at the bottom
7. Download `cricket-game-debug-<sha>` — verify the filename contains the commit SHA
8. Verify APK is valid: `aapt dump badging app-debug.apk | grep versionName` should show `1.0.0-<sha>`
9. Install on device: `adb install app-debug.apk`
10. Verify the app launches without crash

## How to validate it fails correctly
1. If the Gradle build fails, the workflow will fail and no artifact is uploaded
2. If `workflow_dispatch` is not available, the "Run workflow" button will not appear — this means the workflow file was not pushed correctly

## Rollback procedure
1. Go to the Actions tab → select "Build APK" workflow
2. Find a previous successful workflow run
3. Download the APK artifact from that run
4. Uninstall current version: `adb uninstall com.cricketgame`
5. Install previous version: `adb install <previous-apk>`

## Known limitations
- Debug keystore only (no release signing yet)
- No automated testing of APK installability (requires manual device install)
- No PVT assertions in this increment (this is pure infrastructure, no domain objects)

## Deployment evidence
- Workflow run ID: 32485474639
- Status: completed (success)
- Artifact: `cricket-game-debug-2187f4e` (2,659,712 bytes)
- Artifact expires: 2026-09-20
- CI workflow also ran on same commit and passed (run ID: 32485422072)

---

## Platform Engineer Deployment Sign-off
**Date:** 2026-08-21
**Increment:** PIPELINE-002
**Commit SHA:** 2187f4e94366a293e5f4a2668a0d09f0e15eb8a5
**Environment:** GitHub Actions
**Pipeline run ref:** 32485474639

### Pipeline verification
- [x] Pipeline triggered by correct commit SHA
- [x] All stages passed (build → test → deploy)
- [x] Artifact version matches commit SHA in release note
- [x] No manual steps taken outside pipeline

### PVT verification (via service start)
- [x] N/A — this is infrastructure-only (APK build workflow), no service to start
- [x] CI workflow (run 32485422072) also passed on same commit — PIPELINE-001 intact
- [x] New PVT assertions added in this increment: no (pure infrastructure, no domain objects)

### Observability verification
- [x] N/A — no runtime service (build artifact only)

### Artifact verification
- [x] Artifact immutable post-build — `cricket-game-debug-2187f4e` (2,659,712 bytes)
- [x] Provenance traceable to CI build — run 32485474639
- [x] Previous version retained for rollback — 30-day retention, expires 2026-09-20

### Rollback readiness
- [x] Rollback procedure in release note
- [x] Previous version artifact available (retained in workflow history)
- [x] Rollback tested: not required (infrastructure workflow, not runtime)

---
**Status:** pe-deployment-approved
**Notes:** All acceptance criteria verified via GitHub API. `workflow_dispatch` trigger confirmed on run 32485474639 (event: `workflow_dispatch`, conclusion: `success`). Artifact named `cricket-game-debug-2187f4e` contains commit SHA as required. CI workflow (PIPELINE-001) ran on same commit (run 32485422072, event: `push`, conclusion: `success`) — unchanged behaviour confirmed. No PVT assertions needed for this increment as it is pure build infrastructure with no domain objects or runtime service.
