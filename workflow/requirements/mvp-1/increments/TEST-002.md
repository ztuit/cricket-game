# TEST-002 — Coverage reporting
**MVP:** 1
**Status:** open
**Priority:** should
**Complexity:** low
**Dependencies:** TEST-001
**Created:** 2026-08-20

---
## Purpose
JaCoCo integrated into the Gradle test stage. HTML report produced on each CI run. Domain-layer coverage visible separately from Android-layer coverage. Coverage trend tracked across increments.

**Ubiquitous language terms involved:**
None — this is test infrastructure.

---
## Acceptance criteria
- [ ] JaCoCo plugin configured in build.gradle.kts
- [ ] `./gradlew test` produces a JaCoCo HTML report
- [ ] Domain-layer coverage (domain.* packages) reported separately
- [ ] Coverage report archived as a GitHub Actions artifact
- [ ] Coverage summary posted to PR comments (via jacoco-report-upload-action or equivalent)
- [ ] No hard coverage threshold for MVP 1 — coverage is a signal, not a gate

---
## Technical notes
**SE:** JaCoCo Gradle plugin configured to produce XML and HTML reports. XML used by CI for PR comments; HTML for human browsing. Domain coverage tracked separately because the domain layer is pure Kotlin and should have the highest coverage. Android/UI layer coverage intentionally lower (instrumented tests are slow). As domain stabilizes, raise to 80% line coverage on domain.* packages.

**Cyber:** None.

**UX:** None.

**Ops:** None.

**DDD:** None.

---
## Deployment validation
1. Run `./gradlew test` locally — verify JaCoCo HTML report is generated
2. Push to a branch — verify CI produces coverage report as artifact
3. Open a PR — verify coverage summary appears in PR comments
4. Verify domain.* coverage is reported separately from other packages
5. Verify no hard threshold blocks the build (coverage is informational)
