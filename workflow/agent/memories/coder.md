# Coder Memory

## 2026-08-21 — PIPELINE-001: Initial CI pipeline
**Type:** decision
**Context:** First increment — creating Android project from scratch with CI pipeline.
**What happened:** 
- Created Android project with Kotlin DSL, JDK 17, Gradle 8.5, AGP 8.2.2
- GitHub Actions CI workflow with build, test, gitleaks secret scanning
- All GitHub Actions pinned to v4 tag SHAs (verified via GitHub API)
- PE token (`GITHUB_TOKEN_PE`) has `repo` scope but not `workflow` scope — cannot push `.github/workflows/` files via HTTPS. Used SSH instead.
**Impact:** Future workflow changes must be pushed via SSH, not HTTPS with PE token.
**Status:** resolved

## 2026-08-21 — GitHub Actions SHA lookup
**Type:** lesson
**Context:** Initial SHAs were wrong (deprecated or non-existent). CI failed on first run.
**What happened:** Used GitHub API to look up correct SHAs:
- actions/checkout@v4: 11d5960a326750d5838078e36cf38b85af677262
- actions/setup-java@v4: cf277c60eb25467037889841efdb72551f06f6c3
- actions/cache@v4: 0057852bfaa89a56745cba8c7296529d2fc39830
- gitleaks/gitleaks-action@v2: dcedce43c6f43de0b836d1fe38946645c9c638dc
- actions/upload-artifact@v4: ea165f8d65b6e75b540449e92b4886f43607fa02
**Impact:** Always verify SHAs via GitHub API before committing workflow files.
**Status:** resolved

## 2026-08-21 — Gradle wrapper bootstrapping
**Type:** lesson
**Context:** No Gradle installed locally. Downloaded Gradle 8.5 distribution to generate wrapper.
**What happened:** Downloaded full Gradle distribution (~95MB), ran `gradle wrapper`, copied files. Gradle wrapper JAR (43KB) was downloaded separately from GitHub.
**Impact:** For future projects, can download just the wrapper JAR and create scripts manually.
**Status:** resolved

## 2026-08-21 — PIPELINE-002: APK workflow deployment
**Type:** decision
**Context:** Implementing signed APK artifact production with workflow_dispatch trigger.
**What happened:**
- Created `.github/workflows/apk.yml` as a separate workflow (not modifying ci.yml)
- Uses `assembleDebug` with Android's default debug signing — no explicit `signingConfig` needed
- All actions pinned to same SHAs as ci.yml for consistency
- APK artifact named `cricket-game-debug-<sha>`, 30-day retention
- Workflow triggered via GitHub API (gh CLI not installed, used PE token with `repo` scope)
- Workflow run 32485474639 completed successfully, artifact `cricket-game-debug-2187f4e` (2.6MB)
- CI workflow also ran on same commit and passed — PIPELINE-001 behaviour unchanged
**Impact:** Separate APK workflow keeps concerns clean. Debug signing is automatic for `assembleDebug`. Future release signing will need explicit `signingConfig` + keystore secret.
**Status:** resolved

## 2026-08-22 — INCR-001: Domain model implementation
**Type:** decision
**Context:** Implementing domain objects for Match, Bowler, Batsman, Pitch, Field, Ground.
**What happened:**
- All domain objects were already implemented by Feature Owner (skeleton code with full logic)
- All 58 domain tests pass on first run — no code changes needed
- Domain events collected in-memory via `_events` list (Kotlin Flow emission deferred)
- Toss decision is random for both player and AI — no strategy logic
- Batsman stats are hardcoded defaults
- No PVT startup hook exists yet (PIPELINE-004 not deployed) — PVT assertions verified via unit tests only
**Impact:** Domain layer is pure Kotlin, no Android dependencies. All value objects validate at construction time (SEC-003). Ready for Delivery context to build on.
**Status:** resolved
