# PIPELINE-004 — Test device deployment via Firebase App Distribution
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** medium
**Dependencies:** PIPELINE-002
**Created:** 2026-08-20

---
## Purpose
APK can be pushed to test devices via Firebase App Distribution for easy install, replacing manual ADB installation. Crashlytics SDK is integrated to capture crashes from test devices.

**Ubiquitous language terms involved:**
None directly — this is infrastructure.

---
## Acceptance criteria
- [ ] Firebase project configured with Crashlytics
- [ ] APK pushed to Firebase App Distribution on merge to main
- [ ] Tester receives install notification on device
- [ ] APK installs and launches on physical device
- [ ] Crashlytics reports crashes from test device within 5 minutes

---
## Technical notes
**SE:** Add firebase-appdistribution and firebase-crashlytics Gradle plugins. Configure google-services.json (committed as client config, not a secret). App Distribution plugin uploads the APK on merge to main. Crashlytics SDK initialized in Application.onCreate().

**Cyber:** google-services.json is client config, not a secret. Firebase API keys are project identifiers. Privacy policy required before Play Store listing (SEC-001) but not blocking for test-device distribution.

**UX:** None. Crashlytics dialog may appear on first launch (opt-in) which is acceptable for test devices.

**Ops:** Crashlytics custom keys to set on match start: matchId, groundId, weather. Enables crash context without PII.

**DDD:** None.

---
## Deployment validation
1. Merge a change to main
2. Verify APK appears in Firebase App Distribution console
3. Verify install notification received on test device
4. Install and launch the APK
5. Trigger a deliberate crash (e.g., divide by zero in a test path)
6. Verify crash appears in Firebase console within 5 minutes with matchId custom key
