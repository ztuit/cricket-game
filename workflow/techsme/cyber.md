# Security & Cyber Assessment — Cricket Game
**Status:** draft

---

## Full Technical Detail

### Threat model

**Attack surface summary:**

MVP 1 attack surface is **minimal by design**. The app is fully offline with no network calls, no user accounts, no server component, and no sensitive data. The only attack surface is the local Android application binary and its local SQLite database.

| Surface | Exposure | Notes |
|---|---|---|
| APK binary | Local device only | Reverse engineering possible but low consequence — no secrets, no PII, no monetisation |
| Room database (SQLite) | Local device only | Game state only. No sensitive data. Rooted device can read it — low consequence. |
| Firebase Crashlytics SDK | Network (Google servers) | Collects device info, crash traces. No PII from app itself. Privacy policy required for Play Store. |
| Build pipeline (GitHub Actions) | CI/CD | Standard supply chain surface. |

**Threat actors:**

| Actor | Motivation | Capability | Priority |
|---|---|---|---|
| Casual reverse engineer | Curiosity, modding | Can decompile APK, read local DB | Low — no valuable secrets or PII |
| Cheat tool user | Inflate scores locally | Can modify local game state | Low — no online leaderboards in MVP 1, local scores are inconsequential |
| Supply chain attacker | Compromise build pipeline | Dependency poisoning, CI compromise | Low probability, high impact — mitigated by dependency verification |

**Assessment:** No critical or high threat actors applicable to MVP 1. The offline, no-PII, no-server architecture eliminates the primary attack vectors that would require blocking findings.

---

### STRIDE analysis

| Category | Finding | Severity | Mitigation | Status |
|---|---|---|---|---|
| Spoofing | No authentication surface — no accounts, no server calls | N/A | None needed for MVP 1 | N/A |
| Tampering | Local SQLite database could be modified on rooted devices | Low | No consequence in MVP 1 — no leaderboards, no purchases, no PII. Domain validation in value objects provides in-memory integrity. | Accepted |
| Tampering | Match state serialisation could be corrupted | Low | Room handles integrity. Domain invariants re-validate on deserialisation. | Accepted |
| Repudiation | No audit trail for player actions | Low | No consequence — single-player offline game. Analytics interface reserved for future if needed. | Accepted |
| Information disclosure | Firebase Crashlytics collects device information | Medium | Privacy policy required for Google Play listing. Disclose data collection. No PII from app itself. | Open — see SEC-001 |
| Information disclosure | Local DB readable on rooted devices | Low | No sensitive data stored. Game state is not confidential. | Accepted |
| Denial of service | Malformed saved state could crash app on load | Low | Room schema validation. Domain invariants reject invalid state. Defensive deserialisation. | Accepted |
| Elevation of privilege | No privilege model — single-user offline app | N/A | None needed for MVP 1 | N/A |

**Summary:** No critical or high findings. One medium finding (SEC-001) related to Firebase Crashlytics privacy policy, which is a compliance requirement, not a security vulnerability.

---

### Software provenance

**Dependency management:**

| Concern | Approach | Risk |
|---|---|---|
| Build tool | Gradle with Kotlin DSL | Standard Android. Well-maintained. |
| Dependency resolution | Gradle dependency lock files | Prevents unexpected version changes. |
| Core dependencies | Room, Compose, Firebase Crashlytics — all from Google | Low supply chain risk — first-party Android libraries. |
| Transitive dependencies | Gradle dependency tree, reviewed at each MVP | Could include less-maintained libraries. |
| Dependency verification | Gradle signature verification where available | Not all libraries sign artifacts. |

**Supply chain risks:**

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| Compromised Google library | Very Low | High | Pin dependency versions. Review changelogs before upgrading. |
| Compromised transitive dependency | Low | Medium | `./gradlew dependencies` audit before each MVP. Use `dependencyLocking` in Gradle. |
| CI pipeline compromise | Low | High | GitHub Actions pinned to SHA, not tag. Secrets not in repo. Signed commits recommended. |

**Recommended controls:**
1. Enable Gradle dependency verification (`verification-metadata.xml`)
2. Pin GitHub Actions to commit SHAs, not version tags
3. Audit `./gradlew dependencies` output before each MVP gate
4. No secrets committed to repository (pre-commit hook or CI scan)

---

### Data protection

| Domain object | Sensitivity | At-rest | In-transit | Retention |
|---|---|---|---|---|
| Match | None — game state only | Room (SQLite), unencrypted | N/A — no network | Until app uninstall or user deletes |
| Over | None | Room, within Match | N/A | Same as Match |
| Delivery | None | Room, within Match | N/A | Same as Match |
| Bowler | None — fictional character | Room, unencrypted | N/A | Until app uninstall |
| Batsman | None — fictional character | Room, unencrypted | N/A | Until app uninstall |
| Pitch | None — match conditions | Room, within Match | N/A | Same as Match |
| Ground | None — public venue names | Room, static data | N/A | Until app uninstall |
| Field Placement | None — tactical data | Room, static data | N/A | Until app uninstall |
| InningsProgress | None — derived state | Room, within Match | N/A | Same as Match |
| All Value Objects | None | Embedded in parent entities | N/A | Same as parent |

**Assessment:** No domain object in MVP 1 requires encryption, access control, or special retention handling. All data is non-sensitive game state. This changes in MVP 3 (IAP receipts) and MVP 4 (player accounts, cloud saves).

---

### Security requirements (blocking)

| Requirement | Priority | From MVP | Acceptance criteria |
|---|---|---|---|
| SEC-001: Privacy policy for Firebase Crashlytics | high | MVP 1 | Privacy policy published and linked from Play Store listing. Discloses device data collection by Crashlytics. |
| SEC-002: No secrets in source code or committed config | critical | MVP 1 | CI scan (e.g., `gitleaks` or equivalent) passes on every push. No API keys, tokens, or credentials in repository history. |
| SEC-003: Domain input validation enforced | critical | MVP 1 | All value objects enforce range checks as defined in ddd.md. Invalid inputs rejected before reaching game engine. |
| SEC-004: No PII collection | critical | MVP 1 | App makes no network calls except Crashlytics. No user-identifiable data stored beyond device-local game state. |
| SEC-005: Code obfuscation enabled for release builds | medium | MVP 1 | ProGuard/R8 enabled. Release APK is obfuscated. |
| SEC-006: IAP receipt server-side validation | critical | MVP 3 | Google Play Billing receipts validated server-side, not client-side. Client-only validation is spoofable. |
| SEC-007: Leaderboard anti-cheat (server-side validation) | critical | MVP 4 | Match scores submitted to leaderboard are validated server-side. Server re-simulates or checksums match state. |
| SEC-008: Player account data encrypted at rest (server) | critical | MVP 4 | Player PII and cloud saves encrypted at rest on server. TLS for all client-server communication. |

---

### Security actions

| Action | Priority | Owner | Target MVP | Status |
|---|---|---|---|---|
| SEC-001: Create and publish privacy policy for Crashlytics | high | Coder | MVP 1 | Open |
| SEC-002: Add secret scanning to CI pipeline | critical | Platform Engineer | MVP 1 | Open |
| SEC-003: Implement value object validation per ddd.md | critical | Coder | MVP 1 | Open |
| SEC-004: Verify no PII collection (app audit) | critical | Coder | MVP 1 | Open |
| SEC-005: Enable ProGuard/R8 for release builds | medium | Platform Engineer | MVP 1 | Open |
| SEC-006: Design server-side IAP validation architecture | critical | Senior Engineer | MVP 3 | Deferred |
| SEC-007: Design leaderboard anti-cheat architecture | critical | Senior Engineer | MVP 4 | Deferred |
| SEC-008: Design server-side encryption for player data | critical | Senior Engineer | MVP 4 | Deferred |

---

### Example code / patterns

**[ILLUSTRATIVE ONLY — not production code. Labelled as such.]**

#### Example: Value object input validation (Kotlin)

This illustrates how domain value objects enforce their own invariants, rejecting invalid data before it reaches the game engine.

```kotlin
// ILLUSTRATIVE ONLY — example pattern, not production code

/**
 * BallCharacteristics value object — validates delivery parameters.
 * Invalid inputs are rejected at construction time.
 */
data class BallCharacteristics(
    val line: Line,
    val length: Length,
    val pace: Float,  // 0.0 to 1.0
    val spin: Float   // 0.0 to 1.0
) {
    init {
        require(pace in 0f..1f) { "Pace must be between 0 and 1, got $pace" }
        require(spin in 0f..1f) { "Spin must be between 0 and 1, got $spin" }
    }

    enum class Line { OFF_STUMP, MIDDLE, LEG, OUTSIDE_OFF, OUTSIDE_LEG }
    enum class Length { FULL, GOOD_LENGTH, SHORT, YORKER }
}

/**
 * SurfaceCondition value object — enforces monotonic degradation.
 * Degradation cannot decrease once set.
 */
data class SurfaceCondition(
    val zoneId: String,
    val degradation: Float,  // 0.0 to 1.0
    val moisture: Float,     // 0.0 to 1.0
    val roughness: Float     // 0.0 to 1.0
) {
    init {
        require(degradation in 0f..1f) { "Degradation must be between 0 and 1" }
        require(moisture in 0f..1f) { "Moisture must be between 0 and 1" }
        require(roughness in 0f..1f) { "Roughness must be between 0 and 1" }
    }

    /**
     * Apply degradation — returns new SurfaceCondition with
     * degradation that can only increase (monotonic invariant).
     */
    fun degrade(amount: Float): SurfaceCondition {
        val newDegradation = (degradation + amount).coerceAtMost(1f)
        return copy(degradation = newDegradation)
    }
}

/**
 * Outcome value object — enforces dismissal consistency.
 * If outcome is a wicket, dismissal type must be specified.
 */
data class Outcome(
    val type: OutcomeType,
    val runs: Int,
    val dismissalType: DismissalType? = null
) {
    init {
        require(runs >= 0) { "Runs cannot be negative, got $runs" }
        if (type == OutcomeType.WICKET) {
            requireNotNull(dismissalType) {
                "Dismissal type must be specified when outcome is Wicket"
            }
        }
    }

    enum class OutcomeType { RUNS, WICKET, WIDE, NO_BALL, DOT_BALL }
    enum class DismissalType { BOWLED, CAUGHT, LBW, STUMPED, RUN_OUT }
}
```

#### Example: Gradle dependency verification snippet

**[ILLUSTRATIVE ONLY — example pattern, not production code.]**

```kotlin
// build.gradle.kts — ILLUSTRATIVE ONLY

// Enable dependency verification
// Generate with: ./gradlew --write-verification-metadata sha256 help
// Then review and commit the generated verification-metadata.xml

// Pin critical dependencies to known versions
dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.compose:compose-bom:2024.02.00")
    implementation("com.google.firebase:firebase-crashlytics:18.6.1")
}

// dependencyLocking {
//     lockAllConfigurations()
// }
```

#### Example: CI secret scanning step

**[ILLUSTRATIVE ONLY — example pattern, not production code.]**

```yaml
# .github/workflows/ci.yml — ILLUSTRATIVE ONLY

# Secret scanning step to add to CI pipeline
- name: Scan for secrets
  uses: gitleaks/gitleaks-action@v2
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

---

## Plain-Language Summary

### Risk
The security risk for MVP 1 is very low. The game runs entirely on the phone with no internet connection, no user accounts, and no sensitive data. There is nothing valuable for an attacker to steal or tamper with. The main risk is not technical — it is compliance: if we publish to the Play Store with Firebase Crashlytics, we need a privacy policy that discloses what data Crashlytics collects about the device.

### Constraint
There are no security constraints that limit what can be built in MVP 1. The offline architecture is inherently secure — no network means no network attacks. The only constraint is the privacy policy requirement if using Firebase Crashlytics on the Play Store.

### Vision impact
Security has no impact on the MVP 1 vision. The architecture already reserves clean seams for the security-critical features that arrive later: server-side IAP receipt validation in MVP 3, and leaderboard anti-cheat in MVP 4. These are designed in advance, not bolted on after.

### Recommendation
MVP 1 can proceed with no blocking security findings. The two actionable items are: (1) create a privacy policy for Firebase Crashlytics before Play Store listing, and (2) add secret scanning to the CI pipeline as a standard hygiene measure. Both are low effort. The critical security requirements (IAP validation, leaderboard anti-cheat) are correctly deferred to MVPs 3 and 4 — they are designed into the architecture but not built until the features that need them arrive.

---

## PO Approval
**Status:** approved
**Date:** 2026-08-20
**Notes:** Security posture is fully compatible with the product vision. The offline, no-PII, no-server architecture for MVP 1–3 eliminates the primary attack vectors — this is a direct benefit of the vision's offline-first approach. No security findings block MVP 1. The two actionable items (privacy policy for Crashlytics, secret scanning in CI) are low-effort hygiene measures. The critical security requirements are correctly deferred: SEC-006 (IAP server-side validation) aligns with MVP 3 freemium, and SEC-007/SEC-008 (leaderboard anti-cheat, player data encryption) align with MVP 4 online features. These are designed into the architecture now, not bolted on later — exactly right.

---

## MVP increment review notes
| Date | MVP | Increment | Finding | Action |
|---|---|---|---|---|
| — | — | — | — | — |
