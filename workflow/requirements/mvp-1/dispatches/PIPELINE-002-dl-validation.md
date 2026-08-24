# Dispatch — PIPELINE-002 DL Validation
**From:** Platform Engineer
**To:** Delivery Lead
**Date:** 2026-08-21
**Increment:** PIPELINE-002
**Status:** pe-signed-off — ready for DL validation

---

## What to validate
PIPELINE-002 (Signed APK artifact production) has been signed off by the Platform Engineer.

## Release note
`workflow/requirements/mvp-1/releases/PIPELINE-002-2187f4e.md`

## Increment file
`workflow/requirements/mvp-1/increments/PIPELINE-002.md`

## PE verification summary
All checks passed via GitHub API:
- Workflow run 32485474639: `Build APK` workflow, triggered by `workflow_dispatch`, status: completed, conclusion: success
- Commit SHA `2187f4e94366a293e5f4a2668a0d09f0e15eb8a5` confirmed in workflow run
- Artifact `cricket-game-debug-2187f4e` (2,659,712 bytes) produced, named with commit SHA, expires 2026-09-20
- CI workflow (run 32485422072) also passed on same commit — PIPELINE-001 behaviour unchanged

## DL validation steps
Per the release note's "How to validate it works":
1. Go to `https://github.com/ztuit/cricket-game/actions`
2. Select "Build APK" workflow
3. Click "Run workflow" → `master` branch → "Run workflow"
4. Wait ~1–2 minutes for completion
5. Verify artifact `cricket-game-debug-<sha>` appears
6. Download and verify filename contains commit SHA
7. Optionally: install on device via `adb install` and verify app launches

## Acceptance criteria to verify
- [ ] `workflow_dispatch` trigger produces a signed APK on demand
- [ ] APK attached to the GitHub Actions workflow run
- [ ] APK `versionName` contains the short commit SHA
- [ ] APK installable on a physical Android device via ADB
- [ ] Previous version artifact retained in workflow history for rollback
- [ ] CI (build + test) continues to run on every push to main

## Note
This is an infrastructure-only increment — no domain objects, no PVT assertions needed. The validation is about confirming the workflow and artifact are functional, not about app behaviour.
