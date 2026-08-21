# Technical Architecture — Cricket Game
**Status:** draft
**Version:** v1
**Date:** 2026-08-20

---

## Grill-me: Ambiguities forcing premature commitment

Before committing to technology choices, these ambiguities must be surfaced:

### 1. Visual style — the biggest risk

The product vision lists "2D top-down or comic book" and says "we will have to try some things." This is not a cosmetic choice — it determines the rendering technology:

- **2D top-down** → Jetpack Compose Canvas or Android Canvas2D is sufficient. Lightweight, fast to iterate.
- **Comic book style** → May require sprite sheets, frame animation, possibly a 2D engine like LibGDX. Different rendering pipeline entirely.
- **Hybrid** → Compose for UI chrome, engine for gameplay scenes. Most complex.

**Architectural decision:** The game logic (physics, match state, delivery resolution) must be completely decoupled from rendering. The rendering layer is an interface the game engine writes to. This means the visual style decision can be deferred to a prototype spike without blocking core development. However, the spike must happen early in MVP 1 — not after the engine is built — because the rendering approach may influence how the game state is structured (e.g., does the renderer need frame-by-frame ball trajectory, or just start/end positions?).

### 2. Input mechanics — wrist angle dial

The Customer flagged: "A visual dial under time pressure sounds fiddly on a phone screen." The interaction model for shot selection + wrist angle directly affects UI framework choice. If the wrist angle is a precise rotary dial, it needs custom touch handling. If it's a swipe gesture, Compose gestures suffice. This is a UX Expert decision, but the architecture must support either without rewriting the game engine.

**Architectural decision:** The input layer feeds a `ShotSelection` value object into the game engine. The game engine doesn't care how the input was collected — dial, swipe, or tap. The UI layer translates physical gestures into `ShotSelection`. This is already clean in the domain model.

### 3. Surface physics visibility

The Customer's strongest feedback: "If I play a defensive shot and it shoots along the ground for four because of some pitch degradation I couldn't see, that's not fun — that's random." The physics model exists in the domain, but how much of it is exposed to the player is a UX decision. The architecture must support both a "full transparency" mode (show all surface conditions) and a "cricket knowledge" mode (show partial info, let the player infer). This is a rendering concern, not a game logic concern.

**Architectural decision:** The game engine exposes the full `SurfaceCondition` state. The rendering layer decides how much to show. Different "information modes" can be toggled without changing the engine. This also supports difficulty scaling — easier modes show more info.

### 4. Outcome animation requirements

What happens when a delivery is resolved? The Customer asks: "Is there a replay? Do I see what the ball did?" If the outcome includes a ball trajectory animation, the rendering layer needs more data from the engine than just "6 runs scored." If it's a static outcome screen, the current `Outcome` value object is sufficient.

**Architectural decision:** For MVP 1, outcomes are resolved to a value object (runs, dismissal type, trajectory hint). The rendering layer shows a simple result. Ball trajectory animation is deferred — but the `Outcome` value object should include enough data (shot type, ball characteristics, contact point) for a future renderer to animate it. We add a `trajectoryHint` field to `Outcome` as optional data.

---

## Full Technical Detail

### Technology choices

| Concern | Choice | Rationale | Alternatives considered |
|---|---|---|---|
| Language | **Kotlin** | Android's first-class language. Team familiarity. Null safety, coroutines, data classes align with domain model value objects. Strong ecosystem for Android game development. | Java (verbose, no null safety), Dart/Flutter (cross-platform not needed yet, smaller game ecosystem) |
| Framework | **Jetpack Compose** for UI chrome + **Compose Canvas** for gameplay rendering (initially) | Modern declarative UI. Canvas API handles 2D rendering. Compose is the Android standard. Can prototype both visual styles with Canvas. | LibGDX (powerful 2D engine but heavier, different paradigm — only justified if comic book style needs sprite sheets), Unity (overkill for a 2D strategy game, licensing overhead) |
| Data store | **Room** (SQLite) for local persistence | Offline-first. Room is the Android standard for structured local data. Handles match state, player profiles, grounds data. Easy to add server sync later via repository pattern. | SharedPreferences (too simple for match state), Realm (third-party dependency, no clear benefit over Room), No persistence (lose state on app close — unacceptable) |
| Messaging / eventing | **Kotlin Channels / StateFlow** for in-process events | Domain events from DDD model map cleanly to Kotlin Flows. No external message broker needed for offline-first. Architecture supports adding a remote event bus later. | RxJava (heavier, declining in Android ecosystem), LiveData (less flexible than StateFlow) |
| API style | **Repository pattern** with local-first implementation | Clean interface between domain and data. When online features arrive, a remote data source slots in behind the same repository interface. | Direct database access (no seam for future server), GraphQL/REST (not needed for offline-first) |
| Auth | **None for MVP 1–3** | Offline-only until MVP 4. Architecture reserves an `AuthSession` abstraction but doesn't implement it. | Google Sign-In from start (premature, adds dependency) |
| Infrastructure | **GitHub Actions** for CI, **local-first** for runtime | Solo developer. CI runs build + tests on every push. No server runtime needed until MVP 4. | Self-hosted CI (maintenance burden), cloud runtime (no server needed yet) |
| Containerisation | **None for MVP 1–3** | Android app runs on device. No server to containerise. Docker may be relevant for MVP 4 server components. | N/A |

### Architecture

The architecture follows a clean-architecture / hexagonal pattern adapted for Android:

```
┌─────────────────────────────────────────────────────────┐
│                    Android App Layer                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │
│  │  UI Layer   │  │  Rendering  │  │  Input Layer    │  │
│  │  (Compose)  │  │  (Canvas /  │  │  (Gesture →     │  │
│  │             │  │   Engine)   │  │   ShotSelection)│  │
│  └──────┬──────┘  └──────┬──────┘  └────────┬────────┘  │
│         │                │                   │           │
│         └────────────────┼───────────────────┘           │
│                          │                               │
│                    ┌─────▼──────┐                        │
│                    │  Game      │                        │
│                    │  ViewModel │                        │
│                    └─────┬──────┘                        │
├──────────────────────────┼──────────────────────────────┤
│                    Domain Layer (pure Kotlin)            │
│  ┌───────────────────────▼───────────────────────────┐  │
│  │                  Game Engine                       │  │
│  │  ┌─────────┐ ┌──────────┐ ┌─────────┐ ┌────────┐  │  │
│  │  │ Match   │ │Delivery  │ │ Player  │ │ Field  │  │  │
│  │  │ Context │ │Context   │ │ Context │ │Context │  │  │
│  │  └────┬────┘ └────┬─────┘ └────┬────┘ └───┬────┘  │  │
│  │       │           │            │           │       │  │
│  │  ┌────▼───────────▼────────────▼───────────▼────┐  │  │
│  │  │            Pitch Context                     │  │  │
│  │  └─────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────┘  │
│                                                         │
│  Domain events: Kotlin Flows (MatchStarted,             │
│  DeliveryBowled, OutcomeResolved, etc.)                 │
├─────────────────────────────────────────────────────────┤
│                    Data Layer                            │
│  ┌─────────────────┐  ┌──────────────────────────────┐  │
│  │ Room Database   │  │ Repository Interfaces        │  │
│  │ (match state,   │  │ (MatchRepository,            │  │
│  │  player data,   │  │  PlayerRepository,           │  │
│  │  grounds)       │  │  GroundRepository)           │  │
│  └─────────────────┘  └──────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

**Bounded context mapping to modules:**

| Bounded Context | Module | Responsibility |
|---|---|---|
| Match | `domain.match` | Match lifecycle, toss, innings, overs, score, result. Orchestrates deliveries. |
| Delivery | `domain.delivery` | Single ball mechanics. Ball characteristics, shot selection, outcome resolution. The probability model lives here. |
| Player | `domain.player` | Bowler and batsman models. Types, stats, character info. |
| Field | `domain.field` | Field placement sets, fielder positions. Selected based on bowler type. |
| Pitch | `domain.pitch` | Surface condition grid, weather, degradation model. The physics engine. |
| Team | `domain.team` (stub) | Placeholder for MVP 3 career progression. |

**Cross-context dependencies (from DDD context map):**
- Match → Delivery (orchestrates): Match creates deliveries, receives outcomes
- Delivery → Player (uses): Delivery references active bowler and batsman
- Delivery → Field (uses): Delivery references field placement for outcome calculation
- Delivery → Pitch (uses): Delivery references surface condition at ball's zone
- Field → Player (referenced): Field placement selected based on bowler type

**Key architectural principle:** The domain layer has zero Android dependencies. It is pure Kotlin. This means:
- Unit testing the game engine requires no Android emulator
- The rendering layer can be swapped without touching game logic
- Future server-side use of the domain model is possible (leaderboard validation)

### Architectural decision records

| Decision | Choice | Rationale | Consequences |
|---|---|---|---|
| **ADR-001: Rendering abstraction** | Game engine outputs state to a `GameRenderer` interface. Renderer implementation is swappable. | Visual style is undecided. Core loop must be buildable with placeholder graphics. The rendering approach may differ between "2D top-down" and "comic book." | Slight upfront complexity in the engine-renderer boundary. Saves a rewrite if visual style changes. The `GameRenderer` interface will evolve as the prototype spike reveals what data the renderer needs. |
| **ADR-002: Domain purity** | Domain layer is pure Kotlin with no Android, Compose, or framework dependencies. | Enables unit testing without Android emulator. Keeps game logic portable. Clean architecture boundary. | Requires mapping layers between domain objects and Room entities / Compose state. Slight boilerplate, but the testability and flexibility payoff is worth it for a solo developer. |
| **ADR-003: Offline-first with repository seams** | All data access goes through repository interfaces. Local Room implementation is the only implementation for MVP 1–3. | Online features (MVP 4) slot in behind the same interfaces. No architectural change needed to add server sync. | Repository pattern adds a thin abstraction layer. Worth it to avoid retrofitting data access when online features arrive. |
| **ADR-004: Domain events as Kotlin Flows** | Domain events from the DDD model are emitted as Kotlin Flows (StateFlow / SharedFlow). UI subscribes to these. | Clean separation between state mutation (domain) and state observation (UI). Events from the DDD model map directly to Flow emissions. | UI must handle asynchronous state updates. Compose handles this naturally with `collectAsState()`. |
| **ADR-005: Probability model in domain layer** | The outcome resolution (shot suitability × surface conditions × bowler skill → probability distribution) lives in `domain.delivery` as a pure function. | Testable without any UI. Different difficulty levels can swap probability functions. The model can be tuned without touching UI code. | The model must be deterministic given the same inputs (for testing and replay). Randomness is injected via a seed, not `Math.random()`. |
| **ADR-006: Predefined field placement sets** | Field placements are data-driven (JSON/static data), not generated algorithmically. Selected based on bowler type and match state. | Simpler to implement, easier to balance, and the UX Expert can design the sets without code changes. | Limited to predefined sets in MVP 1. Algorithmic generation is a future enhancement if needed. |
| **ADR-007: Single batsman model for MVP 1** | One batsman faces all deliveries. No partnerships, no non-striker, no run-outs. | Simplifies the match model significantly. Customer flagged partnerships as missing — revisit after MVP 1 validation. | Run-out dismissals are excluded. Partnership dynamics (running between wickets) are excluded. These are additive features for later MVPs. |
| **ADR-008: AI target is pre-calculated** | When the player loses the toss, the target is a number generated based on difficulty, not a simulated innings. | Simplest model. No need to simulate 20 overs of AI batting. Keeps MVP 1 scope tight. | The player never sees the AI bat. The target feels arbitrary without context. Mitigation: present it as "the opposition scored X" with flavour text. |

### Build and deployment

**CI/CD approach:**

| Stage | Tool | Trigger | Steps |
|---|---|---|---|
| Build | GitHub Actions | Every push | `./gradlew build` — compile, lint, unit tests |
| Test | GitHub Actions | Every push | `./gradlew test` — domain layer unit tests (pure Kotlin, fast) |
| Android test | GitHub Actions | Every push | `./gradlew connectedAndroidTest` — instrumented tests (requires emulator, slower) |
| Package | GitHub Actions | Merge to main | Produce signed APK/AAB |
| Deploy | Manual | Human-triggered | Install APK on test device via ADB or Firebase App Distribution |

**Environment strategy:**

| Environment | Purpose | How to reach it |
|---|---|---|
| Local dev | Developer machine | Android Studio + emulator or physical device |
| CI | Automated build and test | GitHub Actions runners |
| Test device | Manual testing of gameplay | APK installed via ADB or Firebase App Distribution |
| Production-like | Pre-release validation | Signed AAB uploaded to Google Play internal testing track |

**MVP 1 production-like environment:** A signed APK installed on a physical Android device. No server component. The "production-like" environment IS the device. CI produces the APK; the human installs it and plays.

**Build tool:** Gradle (Android standard). Kotlin DSL for build scripts.

### NFRs

| NFR | Requirement | How it will be met |
|---|---|---|
| Performance | Shot selection response < 100ms from input to outcome display. Match load < 2 seconds. | Domain layer is pure computation (no I/O). Room queries are async. UI state updates via StateFlow (no blocking). |
| Scalability | N/A for offline single-player. Architecture supports future server-side scaling via repository pattern. | Repository interfaces allow adding remote data sources without changing domain logic. |
| Availability | App works fully offline. No network dependency for any gameplay feature. | All data is local (Room). No remote calls in MVP 1–3. |
| Recoverability | Match state persisted after every delivery. App crash resumes from last delivery. | Room auto-save after each delivery. `Match` aggregate is serializable. Resume logic on app start. |
| Data retention | Match history retained indefinitely on device. Player profile retained until uninstall. | Room database. No automatic cleanup in MVP 1. Future: cloud sync in MVP 4. |
| Compliance | Google Play policies. No PII collected in MVP 1–3. No analytics SDK until MVP 3+ (and then only with consent). | No network calls = no data leaves the device. IAP in MVP 3 uses Google Play Billing (compliant by design). |

### Observability

**MVP 1 observability is minimal by design — there is no server to observe.**

| Concern | Approach | MVP 1 requirement |
|---|---|---|
| Logging | Android `Log` with domain-event tags. Structured log entries for match events (delivery bowled, outcome resolved). | Yes — enables debugging gameplay issues from logcat. |
| Crash reporting | Firebase Crashlytics (opt-in, added in MVP 1). | Yes — solo developer needs to know about crashes on test devices. |
| Analytics | None for MVP 1. Architecture reserves an `Analytics` interface that domain events can feed. | No — but the interface exists for MVP 3+. |
| Metrics | None for MVP 1. Future: match completion rate, average score, shot selection distribution. | No |
| Tracing | None for MVP 1. | No |

**Logging standards:**
- Every domain event logged with: event name, match ID, delivery ID (if applicable), timestamp
- Game engine decisions logged at DEBUG level: probability calculations, surface condition changes
- Errors logged with full context: what was the match state when the error occurred

### Security considerations

**Initial security posture: Minimal — offline app with no user data.**

| Concern | MVP 1 status | Future concern |
|---|---|---|
| Data at rest | Match state stored in Room (SQLite). No sensitive data. | MVP 4: user accounts, cloud saves need encryption. |
| Data in transit | No network calls. | MVP 4: TLS for all server communication. |
| Authentication | None. | MVP 4: Google Sign-In or equivalent. |
| Input validation | Domain model validates all inputs (value objects enforce ranges). | Ongoing — domain validation is the first line of defence. |
| Secrets management | No secrets in MVP 1. | MVP 3: Google Play Billing requires API keys (stored in Android Keystore, not in code). |
| Code obfuscation | ProGuard/R8 enabled for release builds. | Standard Android practice. |
| Third-party dependencies | Minimal: Room, Compose, Firebase Crashlytics. All from Google. | Dependency audit before each MVP. |

**Known risk areas for Cyber Expert:**
- Google Play Billing integration in MVP 3 — server-side receipt validation is the secure approach. Client-only validation is spoofable.
- If match state is ever synced to a server (MVP 4), the data must be validated server-side to prevent cheated scores on leaderboards.
- Firebase Crashlytics collects device info — privacy policy needed if published to Play Store.

### Evolutionary architecture notes

**What is deliberately deferred:**

| Feature | When | What the current design enables |
|---|---|---|
| Server / online features | MVP 4 | Repository pattern allows remote data sources behind the same interfaces. Domain events can be forwarded to a server. |
| Visual style change | MVP 1 prototype spike | `GameRenderer` interface decouples rendering from game logic. Swapping renderer doesn't touch domain. |
| Partnerships / run-outs | Post-MVP 1 | `Match` aggregate can be extended with a second batsman entity. `Outcome` already has `RunOut` dismissal type. |
| Bowling gameplay | Future | Domain model has `Bowler` as a full entity. Adding player-controlled bowling is an input-layer change, not a domain change. |
| Career / team progression | MVP 3 | `domain.team` is a stub. The progression system plugs into the match result flow. |
| IAP / freemium | MVP 3 | Architecture reserves a `Monetisation` interface. Google Play Billing slots in behind it. |
| Multiplayer | MVP 4+ | Domain events are serializable. A server can adjudicate deliveries by running the same domain logic. |

**What the current design enables without change:**
- Swapping the rendering approach (2D top-down ↔ comic book ↔ hybrid)
- Adding new bowler types or shot types (data-driven enums)
- Tuning the probability model (pure function, fully unit-testable)
- Adding new grounds (static data, no code change)
- Resuming a match after app kill (serializable aggregate)

---

## Plain-Language Summary
> For the Product Owner only. No jargon.

### Risk
The biggest technical risk is the visual style decision. The game can be built with placeholder graphics, but the final look — 2D top-down vs comic book — requires a different rendering approach. We've designed the game engine to be separate from the visuals, so switching later doesn't mean rewriting the game. But the prototype spike (trying both styles with real cricket scenarios) needs to happen early in MVP 1, not at the end.

### Constraint
Everything runs on the phone. No internet needed, no server costs, no account system — until we decide to add online features in MVP 4. This means MVP 1 is fast to build and cheap to run, but it also means there's no way to see how people are playing until we add analytics later.

### Vision impact
The architecture supports the full product vision — from the core loop in MVP 1 through online leaderboards in MVP 4. Nothing we build now needs to be thrown away. The domain model (how cricket works in the game) is completely separate from how it looks on screen, which means the character system, the physics model, and the shot selection mechanic can all evolve independently.

### Recommendation
Start MVP 1 with the domain layer and game engine in pure Kotlin — no Android dependencies, fully unit-testable. Build the rendering layer with Jetpack Compose Canvas as a placeholder. Run the visual style prototype spike in parallel. The core loop (face a delivery, make a choice, see the outcome) can be working on a device within the first few increments, even with placeholder graphics.

---

## PO Approval
**Status:** approved
**Date:** 2026-08-20
**Notes:** Architecture fully supports the product vision from MVP 1 through MVP 4. Three critical vision-compatible decisions: (1) Domain purity — the game logic is completely separate from rendering, which means the character system, physics model, and shot selection can evolve independently as the vision matures. (2) Offline-first with repository seams — MVP 1–3 ship without server complexity, but MVP 4 online features slot in behind the same interfaces without rewriting the domain. (3) Rendering abstraction — the visual style decision (2D top-down vs comic book) can be deferred to a prototype spike without blocking core development, which directly addresses the vision's highest-rated risk. The freemium model (MVP 3) is architecturally supported via the reserved Monetisation interface. The pre-calculated AI target (ADR-008) is the right call for MVP 1 scope — it keeps the core loop tight for validation.

---

## MVP increment review notes
| Date | MVP | Increment | Finding | Action |
|---|---|---|---|---|
| — | — | — | — | — |
