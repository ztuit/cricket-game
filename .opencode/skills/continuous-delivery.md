---
name: continuous-delivery
description: Enforces Continuous Delivery principles, master-only trunk-based development, lightweight microservice architecture, Keepie secret handling, native observability, and DORA optimizations.
triggers:
  - architecture review
  - branching strategy
  - deployment plan
  - refactoring plan
  - feature design
  - tech stack proposal
---

# Continuous Delivery & Incremental Architecture Skill

## Core Mission
You must ensure that all proposed technical work, architecture, code modifications, and deployment plans adhere strictly to **Continuous Delivery (CD)** principles and lean, high-performing technical practices. Reject or reformulate any proposal that relies on heavy frameworks (e.g., Spring), long-lived branches, third-party monoliths/SaaS crutches, big-bang deployments, or breaking schema/API changes. All development centers strictly on `master` with ultra-lightweight, native code bases.

---

## 1. Primary Delivery Principles

1. **`master` is Always in a Deployable State**
   * The `master` branch must be deployable to production at any given moment.
   * Every commit merged to `master` must be small enough to go safely to production without waiting for a release bundle.

2. **Decouple Deployment from Release**
   * **Deployment** is a technical event (moving code on `master` into environments).
   * **Release** is a business event (exposing features to users via toggles or routing).
   * Incomplete or dark code must be continuously deployed to production behind feature toggles before it is activated for end users.

3. **Additive Development (No Breaking Changes)**
   * Never alter or delete existing contracts (APIs, DB columns, event payloads) in a single step.
   * All changes must be strictly backward and forward compatible using the **Expand and Contract** pattern.

4. **Target High DORA Performance**
   All architectural and delivery decisions must explicitly optimize for the four DORA metrics:
   - **Deployment Frequency:** Daily or multi-daily releases to production from `master`.
   - **Lead Time for Changes:** Under 1 hour from `git push` to running in production.
   - **Change Failure Rate:** Target < 5% via additive migrations and feature flag isolation.
   - **Failed Service Recovery Time (MTTR):** Under 15 minutes, prioritized via feature toggle rollbacks over code re-deployments.

---

## 2. Technical Stack & Architectural Guidelines

### A. Minimalist Microservice Philosophy
- **Framework Avoidance:** Strictly avoid heavyweight application frameworks (e.g., Spring, Spring Boot, Jakarta EE). No complex dependency injection magic or opaque lifecycle management.
- **Minimal Third-Party Footprint:** Prefer standard library features over external utilities. Keep third-party application dependencies near zero to minimize supply chain risk and startup latency.
- **Simple, Focused Libraries:** Use lightweight, single-purpose embedded web servers and libraries (e.g., `mu-server` for Java/JVM).
- **Latest Language Features:** Always utilize features of the most up-to-date LTS/current language releases (e.g., Java 21+ Virtual Threads/Loom, Records, Pattern Matching, Sealed Classes) to keep code modern, low-overhead, and concurrency-ready without heavy abstractions.

### B. Secret Management (Keepie Protocol Pattern)
- **No Embedded/Injected Static Secrets:** Services must start without requiring hardcoded, env-var-injected, or disk-persisted sensitive secrets.
- **Keepie Protocol Execution:**
  1. At startup, the service exposes an ephemeral receipt endpoint (e.g., `/password` or `/secret-callback`).
  2. The service sends an authorization request to the Keepie secret broker with the `X-Receipt-Url` header.
  3. Keepie asynchronously posts the required runtime credentials back to the service endpoint.
  4. The service ingests the secret in-memory and immediately closes or restricts the receipt endpoint.

### C. First-Class Observability (O11y)
- **Zero-Blindspot Features:** Every new endpoint, feature toggle, domain logic path, and integration point must emit native telemetry.
- **Standardized Tracing & Metrics:** Integrate OpenTelemetry (or standard lightweight exporters) for distributed tracing, metrics, and structured JSON logs (stdout).
- **Core Telemetry Requirements per Feature:**
  - Standardized RED metrics (Requests, Errors, Duration) for every HTTP route.
  - Custom counters for feature-toggle evaluations (e.g., `feature_flag_evaluated{flag="x", state="enabled"}`).
  - Correlation/Trace IDs propagated across all microservice calls.

---

## 3. Pure Trunk-Based Development (`master` Branch)

- **Single Source of Truth:** All development happens directly on `master` (or short-lived topic branches off `master` targeting `master`).
- **No Long-Lived Branches:** Feature branches, epic branches, release branches, or environment branches (`dev`, `staging`, `prod`) are strictly prohibited.
- **Branch Lifespan:** If a topic branch is used for PRs/code review, it MUST be merged into `master` within hours (never exceeding 24 hours).
- **Continuous Integration to `master`:**
  - Incomplete work MUST be merged into `master` daily.
  - Incomplete logic must be hidden behind **feature toggles** or **dark launches** so that `master` remains continuously green and production-ready at every commit.
- **Environment Parity:** Deployments to test, staging, and production environments are driven by tags, build artifacts, or pipeline steps originating exclusively from `master`—never by merging code between environment branches.

---

## 4. Non-Breaking Schema & API Rules (Expand & Contract)

When proposing or writing code/database changes:

### Database / Schema Changes (3-Phase)
1. **Expand:** Add new columns/tables as optional/nullable. Write code that writes to *both* old and new schemas, but reads from old. Deploy to `master`.
2. **Migrate:** Backfill data asynchronously. Update code to read from the new schema behind a feature toggle. Deploy to `master`.
3. **Contract:** Once 100% verified in production, remove reads/writes to the old schema, then drop the old column/table in a final cleanup commit on `master`.

### API / Contract Changes
1. Add new fields or endpoint versions alongside existing contracts without breaking current signatures.
2. Route traffic via feature flags or lightweight internal handlers.
3. Monitor observability telemetry until old consumer traffic drops to zero before pruning legacy code.

---

## 5. Feature Toggles & Progressive Delivery

- **Feature Flags First:** Wrap incomplete work or new execution paths in dynamic feature flags or simple runtime configs.
- **Flags Are Technical Debt:** Require an explicit cleanup task once a flag reaches 100% rollout.
- **Deployment Mechanisms:**
  - **Dark Launching:** Code deployed to production, processing duplicate writes or background checks while inactive for end users.
  - **Canary / Blue-Green Deployments:** Route traffic progressively with immediate automated rollback on error rate spikes.

---

## 6. Agent Instructions / Output Checks

When responding to prompts, reviewing designs, or generating code/architecture plans, you **MUST**:

1. **Enforce `master` Branching:** Reject any workflow proposal that mentions `develop`, `release`, `staging`, or environment branches. All integration paths lead directly to `master`.
2. **Verify Tech Stack Alignment:** Reject proposals involving Spring, heavy application servers, or unneeded third-party SaaS/tools. Verify the use of modern language primitives and light libraries like `mu-server`.
3. **Verify Keepie Secret Flow:** Ensure any sensitive credential boot sequence adheres to the async receipt callback pattern.
4. **Enforce Observability:** Ensure every feature proposal includes explicit metrics, tracing, and logging instrumentation.
5. **Verify Non-Breaking Nature:** Explicitly demonstrate how database, API, or service updates use the Expand and Contract pattern to maintain zero downtime.
6. **Chunking & DORA Check:** Verify that changes are chunked into daily, low-risk deployment units targeting fast lead times and instant MTTR.