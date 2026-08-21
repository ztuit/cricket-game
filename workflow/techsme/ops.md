# Operational Support Assessment — Cricket Game
**Status:** draft

---
## Full Technical Detail

### Operational risk areas
| Risk area | Why risky | Mitigation | Priority |
|---|---|---|---|
| Match state corruption | Match state persisted after every delivery. A corrupt write mid-delivery could lose progress or crash the app. Room auto-save is the only persistence mechanism. | PVT assertion validates Room schema on startup. Domain model invariants enforced at write time. Match resume logic tested for partial state. | Critical |
| Silent data loss on app kill | Player expects match to resume. If Room transaction fails silently between deliveries, the player loses a match they thought was saved. | Structured log entry on every save. Error logged with matchId and deliveryId. Crashlytics captures any Room exceptions. | High |
| Crash during delivery resolution | Core gameplay loop — if the probability model or outcome resolution throws, the player sees a crash mid-shot. Most damaging possible crash location. | Pure domain layer = fully unit-testable. Crashlytics captures with match state context. PVT asserts domain objects instantiate cleanly. | High |
| Firebase Crashlytics device info collection | Crashlytics collects device info by default. Google Play requires privacy policy disclosure. Not a data leak but a compliance gap if not disclosed. | Privacy policy drafted before Play Store listing. Crashlytics opt-in toggle for future consent management. | Medium |
| Performance degradation on low-end devices | Shot selection under time pressure (<100ms target). If rendering or state updates lag on budget Android devices, the core loop breaks. | Domain layer is pure computation — no I/O. StateFlow updates are non-blocking. Performance profiling on target device range before MVP 1 ship. | Medium |
| Room database growth over time | Match history retained indefinitely. On a device with limited storage, an active player could accumulate significant data. | No automatic cleanup in MVP 1. Document storage expectations. Future: match history pruning or cloud sync in MVP 4. | Low |

### Observability requirements

#### Logging
| What to log | Why | Format | Level | Must be in MVP 1 |
|---|---|---|---|---|
| **MatchStarted** event | Know when a match begins — context for all subsequent logs | Structured: `{event: "MatchStarted", matchId, groundId, weather, tossResult, timestamp}` | INFO | Yes |
| **DeliveryBowled** event | Trace each delivery — core gameplay unit | Structured: `{event: "DeliveryBowled", matchId, deliveryId, bowlerId, ballCharacteristics, timestamp}` | INFO | Yes |
| **OutcomeResolved** event | Know what happened — runs, wicket, wide, etc. | Structured: `{event: "OutcomeResolved", matchId, deliveryId, outcome, runsScored, isWicket, dismissalType, timestamp}` | INFO | Yes |
| **WicketFallen** event | Track dismissals — key match events | Structured: `{event: "WicketFallen", matchId, wicketNumber, batsmanId, dismissalType, deliveryId, timestamp}` | INFO | Yes |
| **BoundaryScored** event | Track 4s and 6s — player satisfaction signal | Structured: `{event: "BoundaryScored", matchId, runs, batsmanId, deliveryId, timestamp}` | INFO | Yes |
| **OverCompleted** event | Track over boundaries — match progress | Structured: `{event: "OverCompleted", matchId, overNumber, runsThisOver, wicketsThisOver, timestamp}` | INFO | Yes |
| **InningsCompleted** event | Match result — end-of-game context | Structured: `{event: "InningsCompleted", matchId, finalScore, wicketsFallen, oversCompleted, result, timestamp}` | INFO | Yes |
| **MatchCompleted** event | Final outcome — summary for analytics | Structured: `{event: "MatchCompleted", matchId, result, finalScore, timestamp}` | INFO | Yes |
| **SurfaceConditionChanged** event | Pitch degradation — debug physics model | Structured: `{event: "SurfaceConditionChanged", matchId, zoneId, newCondition, timestamp}` | DEBUG | Yes |
| **BowlerChanged** event | Bowler rotation — match flow | Structured: `{event: "BowlerChanged", matchId, newBowlerId, bowlerType, timestamp}` | INFO | Yes |
| **FieldPlacementChanged** event | Field changes — tactical context | Structured: `{event: "FieldPlacementChanged", matchId, newFieldPlacement, timestamp}` | INFO | Yes |
| Room database errors | Data layer failures — must never be silent | Structured: `{event: "DataLayerError", operation, matchId, deliveryId, error, timestamp}` | ERROR | Yes |
| Probability model decisions | Debug gameplay balance — why an outcome happened | Structured: `{event: "OutcomeCalculation", matchId, deliveryId, inputs, probabilityDistribution, selectedOutcome, timestamp}` | DEBUG | Yes |
| Match save success/failure | Data integrity — confirm persistence worked | Structured: `{event: "MatchSaved", matchId, deliveryId, success, timestamp}` | INFO | Yes |
| App crash context | Crashlytics captures with match state | Via Firebase Crashlytics SDK — custom keys: matchId, deliveryId, inningsProgress | ERROR | Yes |

**Logging standards:**
- All log entries use structured JSON format via Android `Log` with a consistent tag prefix (`CricketGame.`)
- Every gameplay log entry includes `matchId` and `deliveryId` (where applicable) as correlation context
- No PII in any log entry — there is no user identity in MVP 1
- Log levels: INFO for domain events, DEBUG for internal calculations, ERROR for failures
- Crashlytics custom keys set on match start: `matchId`, `groundId`, `weather`
- Logs are local to the device (logcat). No remote log aggregation until MVP 4 when a server exists.

#### Metrics
| Metric | Why it matters | Alert threshold | Must be in MVP 1 |
|---|---|---|---|
| Match completion rate | Core engagement — do players finish matches? | N/A (no server — tracked via analytics interface stub) | No — analytics interface reserved, not implemented |
| Average score per match | Gameplay balance — too easy or too hard? | N/A | No |
| Shot selection distribution | Player behaviour — which shots are used/ignored? | N/A | No |
| Delivery resolution latency | Performance — must stay under 100ms | N/A (device-local — profiled during testing) | No — profiled manually on test devices |
| Crash rate per match | Stability — crashes per match played | Any crash is a defect | Yes — via Crashlytics |
| Room save failure rate | Data integrity — silent data loss | Any failure logged and surfaced | Yes — logged, no threshold (any failure is investigated) |

**Note:** MVP 1 has no server, so there is no metrics pipeline, no dashboards, and no alerting infrastructure. Metrics are either device-local (logcat) or reported via Crashlytics. The SE's architecture reserves an `Analytics` interface for MVP 3+.

#### Tracing
No distributed tracing in MVP 1 — there is only one process (the Android app). Correlation is achieved via `matchId` + `deliveryId` in structured logs. When a crash occurs, Crashlytics custom keys provide the match context.

Future (MVP 4): When a server exists, OpenTelemetry traces will propagate from client to server for leaderboard submissions, account sync, and multiplayer adjudication. The domain events are already designed to be serializable.

#### Alerting
| Alert | Condition | Severity | Who notified | Must be in MVP 1 |
|---|---|---|---|---|
| App crash | Crashlytics reports a crash | Critical | Developer (Crashlytics dashboard + email notification) | Yes |
| Room database exception | Any Room write/read failure | High | Developer (logcat + Crashlytics) | Yes |
| ANR (Application Not Responding) | Android ANR detected | High | Developer (Google Play Console / Crashlytics) | Yes |
| Performance regression | Delivery resolution exceeds 200ms on test device | Medium | Developer (manual profiling, flagged in CI if benchmark test exists) | No — manual profiling in MVP 1 |

**Alert ownership:** All alerts in MVP 1 notify the solo developer via Firebase Crashlytics dashboard and email. There is no on-call rotation — one person.

### Support runbook requirements
| Scenario | Complexity | Target MVP for runbook |
|---|---|---|
| Player reports match lost after app crash | Low | MVP 1 — document how to check Crashlytics for crash context, how to verify Room state |
| Player reports incorrect outcome (e.g., "I hit a six but it showed caught") | Medium | MVP 1 — document how to check logs for OutcomeResolved event, verify probability model inputs |
| Player reports match won't load / stuck on loading screen | Medium | MVP 1 — document how to check Room schema version, clear app data, verify PVT assertions |
| Player reports performance lag during shot selection | Medium | MVP 2 — needs profiling methodology, device-specific guidance |
| Room database corruption | High | MVP 2 — document recovery procedure, Room export/import, database inspection tools |
| Firebase Crashlytics not reporting | Low | MVP 1 — document SDK initialization check, network requirements for Crashlytics upload |
| Match state migration after schema change | High | MVP 2+ — document Room migration procedure, testing strategy for migrations |

**Note:** Runbooks for MVP 1 are written for the solo developer supporting their own test devices. They are not end-user support documents. When the game reaches Play Store (MVP 2+), runbooks expand to cover user-reported issues.

### Data management
**Backup:**
- No cloud backup in MVP 1 — match data lives only on the device
- Room database file is the single source of truth
- Player can lose all data on uninstall or factory reset — this is acceptable for MVP 1 (test devices)
- Future (MVP 4): cloud save via account system

**Integrity checks:**
- Domain model invariants enforced at write time (e.g., wickets ≤ 10, balls ≤ 6 per over)
- Room transactions ensure atomic writes — a delivery outcome is saved as a single transaction
- PVT assertion validates Room schema is correct on startup
- Match resume logic verifies state consistency on app restart (e.g., InningsProgress values within valid ranges)

**Recovery:**
- App crash → Room auto-recovers on next launch (SQLite WAL mode)
- Corrupt Room state → clear app data (destructive — loses all matches)
- No point-in-time recovery in MVP 1
- Future: Room export/import for debugging, cloud backup for recovery

**Retention:**
- Match history: retained indefinitely on device (no automatic cleanup)
- Player profile: retained until uninstall
- No PII collected — no GDPR/privacy retention requirements for MVP 1
- Crashlytics data: retained per Google's default (90 days)
- **Cross-ref Cyber:** No sensitive data in MVP 1. When accounts arrive (MVP 4), user data classification and retention policies must be defined.

### Deployment and release
**Rollback capability:**
- APK-based deployment — rollback = install previous APK version
- Room database schema versioning — rollback may require clearing app data if schema changed
- No feature flags in MVP 1 — features are compiled in or out
- Future: feature flags for gradual rollout (MVP 2+)

**Feature flags:**
- Not needed in MVP 1 — solo developer, single build
- Architecture reserves the concept: domain events can be tagged with feature flag state
- Future: Android feature flag library (e.g., Firebase Remote Config) for A/B testing shot selection UI

**Zero-downtime requirements:**
- Not applicable — offline app, no server downtime
- App update = install new APK over old one (standard Android update flow)
- Match in progress during update: Room state preserved if schema is compatible

### Known operational complexity points
1. **No remote observability in MVP 1.** If a crash happens on a test device that isn't connected to the developer's machine, Crashlytics is the only way to know. This means Crashlytics SDK integration is a hard dependency, not optional.

2. **Match state is the most critical data.** A single corrupt write can lose a player's entire match. The auto-save-per-delivery pattern means ~120 writes per match. Each one must be atomic and logged.

3. **Probability model debugging is hard without structured logs.** When a player says "that outcome was wrong," the only way to verify is to reconstruct the exact inputs (ball characteristics, shot selection, surface condition, bowler stats) from logs. DEBUG-level logging of OutcomeCalculation is essential for this.

4. **Room schema migrations will get harder over time.** MVP 1 has one schema. By MVP 3, there will be multiple tables with foreign keys. Each migration must be tested on real data. Start documenting migration strategy now, even if MVP 1 doesn't need one.

5. **Crashlytics is a third-party dependency.** If Google changes Crashlytics terms or pricing, we need an alternative. The logging strategy (structured logs to logcat) ensures we always have a fallback.

6. **No server means no remote kill switch.** If a critical bug ships in an APK, the only fix is to ship a new APK. There's no way to remotely disable a feature or force an update. This is acceptable for MVP 1 (small test audience) but becomes a risk at Play Store scale.

### Operational requirements (blocking)
| Requirement | Priority | From MVP | Acceptance criteria |
|---|---|---|---|
| Structured logging for all domain events | Critical | MVP 1 | Every event in ddd.md's Domain Events table has a corresponding structured log entry with matchId and deliveryId |
| Firebase Crashlytics integrated | Critical | MVP 1 | Crash on test device produces Crashlytics report with matchId custom key |
| Room save errors logged at ERROR level | Critical | MVP 1 | Any Room write failure produces a structured log entry with match context |
| PVT assertion validates Room schema | Critical | MVP 1 | Service (app) fails to start if Room schema is inconsistent |
| Match state resume tested | High | MVP 1 | App killed mid-match resumes from last saved delivery with correct InningsProgress |
| Delivery resolution performance profiled | High | MVP 1 | Shot selection to outcome display < 200ms on minimum target device |
| No PII in any log or crash report | High | MVP 1 | Audit all log entries and Crashlytics custom keys — no user identity, no device identifiers beyond Crashlytics defaults |
| Privacy policy covers Crashlytics data collection | Medium | MVP 2 | Privacy policy document references device info collection by Crashlytics |

### Operational actions
| Action | Priority | Owner | Target MVP | Status |
|---|---|---|---|---|
| Integrate Firebase Crashlytics SDK | Critical | Coder | MVP 1 | Open |
| Implement structured logging for all domain events | Critical | Coder | MVP 1 | Open |
| Write PVT assertion for Room schema validation | Critical | Coder | MVP 1 | Open |
| Profile delivery resolution latency on target devices | High | Coder | MVP 1 | Open |
| Document match state resume behaviour and edge cases | High | Coder | MVP 1 | Open |
| Write runbook: crash investigation via Crashlytics + logs | Medium | Ops Support | MVP 1 | Open |
| Write runbook: match state corruption recovery | Medium | Ops Support | MVP 2 | Open |
| Draft privacy policy covering Crashlytics | Medium | Cyber Expert | MVP 2 | Open |
| Define Room schema migration strategy | Medium | Senior Engineer | MVP 2 | Open |
| Define analytics interface for match completion metrics | Low | Senior Engineer | MVP 3 | Open |

---
## Plain-Language Summary
### Risk
The biggest operational risk is match state corruption. The game saves after every single delivery — that's up to 120 writes per match. If any one of those writes fails silently, the player could lose their match without knowing why. We mitigate this with structured logging on every save, Room's built-in transaction safety, and crash reporting via Firebase Crashlytics.

### Constraint
MVP 1 has no server, which means no remote monitoring, no dashboards, and no way to see what's happening on a player's device unless they report it or Crashlytics catches a crash. This is fine for a solo developer testing on their own devices, but it means every crash and every data issue must be caught by Crashlytics or by the developer manually checking logs.

### Vision impact
The operational approach supports the full vision. Structured logging with domain event names and match IDs creates the foundation for analytics in MVP 3 and server-side monitoring in MVP 4. Nothing we build now needs to be thrown away — the log format, the Crashlytics integration, and the data integrity patterns all carry forward.

### Recommendation
Ship MVP 1 with structured logging and Crashlytics as hard requirements — not nice-to-haves. Every domain event must be loggable, and every crash must be reportable. The probability model's DEBUG logs are essential for gameplay balance tuning — without them, we can't explain why an outcome happened. Runbooks for MVP 1 are simple (one developer, test devices), but they must exist so that crash investigation has a repeatable process.

---
## PO Approval
**Status:** approved
**Date:** 2026-08-20
**Notes:** Operational approach is compatible with the product vision. The structured logging strategy using domain event names and match IDs creates the foundation for analytics in MVP 3 and server-side monitoring in MVP 4 — nothing built now needs to be thrown away. The match state corruption risk (120 writes per match) is the right thing to call out as the primary operational concern; the mitigation (structured logging, Room transactions, Crashlytics) is pragmatic for a solo developer. The probability model DEBUG logging is essential for the vision's gameplay balance requirement — without it, we cannot explain why outcomes happen, and the "skill not luck" promise breaks down. The Crashlytics privacy policy requirement (medium priority, MVP 2) correctly flags a compliance gap without blocking MVP 1.
