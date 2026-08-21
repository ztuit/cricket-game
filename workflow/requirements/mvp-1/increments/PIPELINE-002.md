# PIPELINE-002 — Signed APK artifact production
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** medium
**Dependencies:** PIPELINE-001
**Created:** 2026-08-20

---
## Purpose
Merge to main produces a signed, installable APK attached to the GitHub Actions workflow run. This is how the game reaches a physical device — CI builds the artifact, a human installs it.

**Ubiquitous language terms involved:**
None directly — this is infrastructure.

---
## Acceptance criteria
- [ ] Merge to main triggers a signed APK build
- [ ] APK attached to the GitHub Actions workflow run
- [ ] APK `versionName` contains the short commit SHA
- [ ] APK installable on a physical Android device via ADB
- [ ] Previous version artifact retained in workflow history for rollback

---
## Technical notes
**SE:** Use Gradle signing config with a debug keystore for test-device builds. Release builds use a proper keystore stored as a GitHub Actions secret. `versionName` set to `"1.0.0-${gitSha}"` where `gitSha` is `git rev-parse --short HEAD`. `versionCode` auto-increments via a CI script or Gradle plugin.

**Cyber:** Keystore secrets stored in GitHub Actions secrets, not in the repository. No secrets committed to source. Debug APK uses debug keystore (acceptable for test devices). Release APK uses release keystore (stored as secret).

**UX:** None — APK is a build artifact, not a user-facing screen.

**Ops:** None — artifact production, not runtime.

**DDD:** None — no domain objects.

---
## Deployment validation
1. Merge a change to main
2. Verify signed APK is produced and attached to the workflow run
3. Download the APK and install on a physical device via `adb install`
4. Verify the app launches without crash
5. Verify `versionName` contains the commit SHA
6. Verify the previous workflow run's APK is still accessible
