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
