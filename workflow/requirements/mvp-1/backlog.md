# MVP 1 Backlog — "The First Ball"
**Status:** ready-for-delivery
**Created:** 2026-08-20
**Branching strategy:** Trunk-based development. Commit directly to main. No PRs, no merge gates. Fix forward on CI failures.
**MVP goal:** Prove the core loop works — a cricket fan can face a delivery, make a meaningful shot selection, and get a satisfying outcome.
**Stakeholder benefit:** The cricket fan gets to test their knowledge in a real match scenario.
**Validation (product level):** A cricket fan plays one full T20 match and says: "The decisions felt real. I knew why I got out, and I want to try again."

---
## Sign-offs required
| Agent | Status | Date | Notes |
|---|---|---|---|
| Senior Engineer | pending | | |
| Cyber Expert | pending | | |
| UX Expert | pending | | |
| Ops Support | pending | | |
| Platform Engineer | pending | | |
| Test Engineer | pending | | |
| Delivery Lead | pending | | |
| Product Owner | pending | | |

**MVP closure blocked until all show approved.**

---
## Increment list
| ID | Title | Priority | Status | Dependencies |
|---|---|---|---|---|
| PIPELINE-001 | CI pipeline: build and unit test | must | done | none |
| PIPELINE-002 | Signed APK artifact production | must | done | PIPELINE-001 |
| PIPELINE-003 | PVT assertion framework | must | open | PIPELINE-001, INCR-001 |
| PIPELINE-004 | Test device deployment via Firebase App Distribution | must | open | PIPELINE-002 |
| PIPELINE-005 | Crashlytics and crash alerting | must | open | PIPELINE-004 |
| TEST-003 | Defect log established | must | open | none |
| INCR-001 | Domain model and match creation with toss | must | done | PIPELINE-001 |
| INCR-002 | Delivery loop — ball delivery, batting input, outcome resolution | must | in-progress | INCR-001 |
| INCR-003 | Scoring and match state tracking | must | open | INCR-002 |
| INCR-004 | Match flow — overs, bowler rotation, match end | must | open | INCR-003 |
| INCR-005 | Visual style spike | must | open | INCR-002 |
| INCR-006 | Field placement and pitch condition display | must | open | INCR-002 |
| INCR-007 | Bowler character system | should | open | INCR-001 |
| INCR-008 | Surface physics model and weather effects | must | open | INCR-002, INCR-006 |
| INCR-009 | Match persistence and resume | must | open | INCR-004 |
| INCR-010 | End-to-end match playtest and structured logging | must | open | INCR-004, INCR-009 |
| TEST-001 | Test framework and fixtures established | should | open | INCR-001 |
| TEST-002 | Coverage reporting | should | open | TEST-001 |

---
## Direction change log
| Date | What changed | Why | Increments affected | Action |
|---|---|---|---|---|
| — | — | — | — | — |

---
## Progress notes
| Date | Note | Agent |
|---|---|---|
| 2026-08-20 | Backlog created from techsme documents | TPO |
| 2026-08-21 | PIPELINE-001 validated and done — CI pipeline fully operational | Delivery Lead |
| 2026-08-21 | PIPELINE-002 dispatched to Coder — signed APK artifact production | Delivery Lead |
| 2026-08-21 | PIPELINE-002 pe-signed-off — APK workflow verified via GitHub API. Ready for DL validation. | Platform Engineer |
| 2026-08-21 | PIPELINE-002 validated and done — all 6 acceptance criteria verified. APK workflow fully operational. | Delivery Lead |
| 2026-08-21 | INCR-001 dispatched — Feature Owner writing tests first, then Coder implements | Delivery Lead |
| 2026-08-22 | INCR-001 validated and done — all 10 acceptance criteria verified. 58 domain tests passing. Domain model foundation complete. | Delivery Lead |
| 2026-08-22 | INCR-002 proposed to human — delivery loop, the core gameplay mechanic | Delivery Lead |
| 2026-08-23 | INCR-002 human-approved and dispatched — Feature Owner writing tests, Coder implementing | Delivery Lead |
