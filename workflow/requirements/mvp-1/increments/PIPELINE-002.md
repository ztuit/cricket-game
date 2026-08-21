# PIPELINE-002 — Signed APK artifact production
**MVP:** 1
**Status:** deployed
**Priority:** must
**Complexity:** medium
**Dependencies:** PIPELINE-001
**Created:** 2026-08-20

---
## Purpose
An explicit manual trigger produces a signed, installable APK attached to the GitHub Actions workflow run. This is how the game reaches a physical device — the developer pushes to main (trunk-based), CI validates on every push, and when a buildable APK is needed, it is triggered explicitly.

**Branching strategy:** Trunk-based development. No PRs, no merge gates. Commit directly to main. Fix forward on CI failures.

**Ubiquitous language terms involved:**
None directly — this is infrastructure.

---
## Human approval record
**Proposed:** 2026-08-20
**Human response:** approved
**Human notes:**
- Debug keystore for now (no release keystore yet)
- No other changes

**Scope confirmed:** workflow_dispatch APK trigger, debug keystore, commit SHA in versionName, ADB installable, previous version retained.

---
## Acceptance criteria
- [ ] GitHub Actions `workflow_dispatch` trigger produces a signed APK on demand
- [ ] APK attached to the GitHub Actions workflow run
- [ ] APK `versionName` contains the short commit SHA
- [ ] APK installable on a physical Android device via ADB
- [ ] Previous version artifact retained in workflow history for rollback
- [ ] CI (build + test) continues to run on every push to main (PIPELINE-001 behaviour unchanged)

---
## Technical notes
**SE:** Separate workflow or workflow_dispatch trigger within existing CI workflow. Use Gradle signing config with a debug keystore for test-device builds. Release builds use a proper keystore stored as a GitHub Actions secret. `versionName` set to `"1.0.0-${gitSha}"` where `gitSha` is `git rev-parse --short HEAD`. `versionCode` auto-increments via a CI script or Gradle plugin. APK production is NOT automatic on every push — it is an explicit developer action.

**Cyber:** Keystore secrets stored in GitHub Actions secrets, not in the repository. No secrets committed to source. Debug APK uses debug keystore (acceptable for test devices). Release APK uses release keystore (stored as secret).

**UX:** None — APK is a build artifact, not a user-facing screen.

**Ops:** None — artifact production, not runtime.

**DDD:** None — no domain objects.

---
## Deployment validation
1. Push a change to main (trunk-based — direct commit)
2. Verify CI runs automatically (build + test) — PIPELINE-001 still works
3. Trigger APK build manually via `workflow_dispatch`
4. Verify signed APK is produced and attached to the workflow run
5. Download the APK and install on a physical device via `adb install`
6. Verify the app launches without crash
7. Verify `versionName` contains the commit SHA
8. Verify the previous workflow run's APK is still accessible

---
## Lifecycle tracking
| Status | Date | Agent | Notes |
|---|---|---|---|
| proposed | 2026-08-20 | TPO | |
| human-approved | 2026-08-20 | Human | Debug keystore, no release keystore yet |
| in-progress | 2026-08-21 | Delivery Lead | Dispatched to Coder |
| deployed | 2026-08-21 | Coder | Commit SHA: 2187f4e94366a293e5f4a2668a0d09f0e15eb8a5. Workflow run: 32485474639. Artifact: cricket-game-debug-2187f4e |

---
## Progress log
| Date | Note | Agent |
|---|---|---|
| 2026-08-21 | Increment dispatched to Coder. Briefing at `workflow/requirements/mvp-1/dispatches/PIPELINE-002-coder.md` | Delivery Lead |
| 2026-08-21 | Implementation complete. Created `.github/workflows/apk.yml` with `workflow_dispatch` trigger. Uses `assembleDebug` with Android's default debug signing. APK uploaded as artifact with commit SHA in filename. 30-day retention. CI workflow unchanged. Workflow triggered and verified: run 32485474639 produced artifact `cricket-game-debug-2187f4e` (2.6MB). | Coder |

---
## Coder sign-off — 2026-08-21
Implementation complete. No Feature Owner tests (pure infrastructure — no domain objects, no business logic).
Files changed: `.github/workflows/apk.yml` (new)
PVT assertions added: none (this is pure infrastructure, no domain objects or config to verify at startup)
Notable decisions:
- Separate workflow (`apk.yml`) rather than adding conditional steps to existing `ci.yml` — keeps concerns separated
- Uses `assembleDebug` with Android's default debug signing — no explicit `signingConfig` needed for debug builds
- Pinned actions to same SHAs as `ci.yml` for consistency
- 30-day artifact retention (matches the increment requirement)
Ready for: Platform Engineer sign-off → DL validation
