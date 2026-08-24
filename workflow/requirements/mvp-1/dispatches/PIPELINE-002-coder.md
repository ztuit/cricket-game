# Coder Dispatch — PIPELINE-002
**Dispatched:** 2026-08-21
**Status:** ready-for-coder
**Increment:** `workflow/requirements/mvp-1/increments/PIPELINE-002.md`

---

## Briefing

You are implementing PIPELINE-002: Signed APK artifact production.

### What to build
Add a `workflow_dispatch` trigger to the existing CI workflow (or create a separate workflow) that produces a signed, installable APK attached to the GitHub Actions run.

### Files to read first
1. `workflow/requirements/mvp-1/increments/PIPELINE-002.md` — acceptance criteria and technical notes
2. `workflow/techsme/senior-engineer.md` — architecture constraints
3. `.github/workflows/ci.yml` — existing CI workflow to extend or reference
4. `app/build.gradle.kts` — current build config (versionName already embeds SHA)

### Existing state
- CI workflow exists at `.github/workflows/ci.yml` with push trigger on all branches
- `versionName` already set to `"1.0.0-$sha"` where sha comes from `-PversionNameSuffix` gradle property
- Build and test stages already pass
- No signing config in `build.gradle.kts` yet

### Implementation requirements

**1. Workflow trigger:**
- Add `workflow_dispatch` trigger to existing CI workflow OR create a separate `apk.yml` workflow
- APK production is NOT automatic on every push — explicit manual trigger only
- PIPELINE-001 behaviour (build + test on push) must remain unchanged

**2. Gradle signing config:**
- Add debug keystore signing config to `app/build.gradle.kts`
- Use Android's default debug keystore (`~/.android/debug.keystore`) for now
- No release keystore yet — that comes later
- No secrets committed to source

**3. APK artifact:**
- Build a debug APK: `./gradlew assembleDebug`
- Upload APK as GitHub Actions artifact with `actions/upload-artifact`
- Artifact name should include commit SHA for traceability
- Retention: 30 days (previous versions retained for rollback)

**4. Version name:**
- Already handled: `versionName = "1.0.0-$sha"` in build.gradle.kts
- Verify it works correctly in the APK output

### Acceptance criteria (from increment file)
- [ ] GitHub Actions `workflow_dispatch` trigger produces a signed APK on demand
- [ ] APK attached to the GitHub Actions workflow run
- [ ] APK `versionName` contains the short commit SHA
- [ ] APK installable on a physical Android device via ADB
- [ ] Previous version artifact retained in workflow history for rollback
- [ ] CI (build + test) continues to run on every push to main (PIPELINE-001 behaviour unchanged)

### Security constraints (from Cyber notes)
- Keystore secrets stored in GitHub Actions secrets, not in the repository
- No secrets committed to source
- Debug APK uses debug keystore (acceptable for test devices)

### After implementation
1. Trigger the workflow manually via GitHub UI or `gh workflow run`
2. Verify the workflow completes successfully
3. Download the APK artifact and verify it's a valid, signed APK
4. If possible, install on a device/emulator via `adb install` to verify
5. Write release note at `workflow/requirements/mvp-1/releases/PIPELINE-002-<sha>.md`
6. Mark increment as `deployed` in the increment file
7. Notify Platform Engineer for sign-off

### Release note template
```markdown
# Release Note — PIPELINE-002
**Commit SHA:** [full SHA]
**Deployed:** YYYY-MM-DD HH:MM UTC
**Environment:** GitHub Actions
**MVP:** 1
**Deployed by:** Coder

---
## What was deployed
[Description of the workflow_dispatch APK trigger]

## How to observe it is running
- GitHub Actions workflow "CI" has a "Run workflow" button enabled
- Manual trigger produces an APK artifact in the workflow run

## How to validate it works
1. Go to Actions tab in GitHub
2. Select "CI" workflow
3. Click "Run workflow" → select branch → click "Run workflow"
4. Wait for workflow to complete
5. Download APK artifact from the workflow run
6. Verify APK filename contains commit SHA
7. Install on device: `adb install <apk-file>`
8. Verify app launches without crash
9. Check versionName: `aapt dump badging <apk> | grep versionName`

## Rollback procedure
1. Previous APK artifacts remain available in prior workflow runs
2. Download and install previous version via `adb install`

## Known limitations
- Debug keystore only (no release signing yet)
- No automated testing of APK installability
```

---

**Coder: begin implementation. Report back when deployed.**
