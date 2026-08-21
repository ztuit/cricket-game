---
name: observability
description: Enforces first-class Observability (O11y) standards, native OpenTelemetry instrumentation, RED/USE metrics, structured JSON logging, feature flag tracking, and actionable alerting.
triggers:
  - o11y
  - observability
  - instrumentation
  - metrics
  - tracing
  - logging
  - telemetry design
---

# Observability (O11y) & Telemetry Skill

## Core Mission
Ensure every feature, microservice, background task, and execution branch is fully observable by default. Reject or reformulate any technical design, PR, or code change that introduces unmonitored execution paths, unhandled silent failures, or unstructured log outputs. Observability is a core delivery constraint—not an afterthought added post-deployment.

---

## 1. Core Observability Principles

1. **Zero-Blindspot Delivery**
   * No feature is complete until it emits structured logs, distributed trace spans, and key operational metrics.
   * Every runtime decision path (especially feature toggles and branch conditions) must be explicitly observable.

2. **Native & Open Standards First**
   * Avoid heavy vendor-proprietary SDKs and monolithic monitoring agents.
   * Standardize strictly on **OpenTelemetry (OTel)** APIs/exporters or native language primitives to ensure zero vendor lock-in and minimal runtime overhead.

3. **High Cardinality & Structured Context**
   * Logs must be output as single-line, structured **JSON to `stdout`**.
   * Correlation IDs (`trace_id`, `span_id`) MUST be injected into all log events and propagated across synchronous (HTTP) and asynchronous (messaging) boundaries.

4. **Telemetry Driven by DORA & MTTR**
   * Observability exists to drive **MTTR under 15 minutes** and detect failure modes before end users experience degradation.
   * Telemetry must immediately reveal whether an anomaly is tied to a specific build, deployment tag, or feature toggle rollout.

---

## 2. The Three Pillars Framework

### A. Metrics (RED & USE Patterns)
Every microservice MUST emit:
* **RED Pattern (for Request/Service Endpoints):**
  - **Rate:** Requests per second (`http_requests_total`).
  - **Errors:** Failed requests by status code (`http_requests_failed_total`).
  - **Duration:** Latency distributions as histograms (`http_request_duration_seconds`).
* **USE Pattern (for Infrastructure & Resource Boundaries):**
  - **Utilization:** % time resource is busy (CPU, memory, connection pool usage).
  - **Saturation:** Queue depth or backlog length waiting for resource.
  - **Errors:** Explicit resource fault counts.
* **Feature Toggle Metrics:**
  - Counter tracking every toggle evaluation: `feature_flag_evaluation_total{flag_name="x", variant="enabled|disabled"}`.

### B. Distributed Tracing
* **Root Spans:** Automatically created at HTTP/RPC boundaries.
* **Context Propagation:** Inject and extract `traceparent` headers across process boundaries.
* **In-Flight Attributes:** Annotate spans with business-safe, non-PII attributes (e.g., `tenant_id`, `route`, `feature_flag_state`).
* **Error Recording:** Explicitly mark spans as `ERROR` and attach exception metadata on failures.

### C. Structured Logging
* **Output:** Clean JSON to stdout. No multi-line stack traces in raw stdout—format exceptions into single-line JSON fields.
* **Mandatory Schema Fields:**
  ```json
  {
    "@timestamp": "2026-07-30T10:21:28Z",
    "level": "INFO|WARN|ERROR",
    "service.name": "order-service",
    "trace_id": "4bf92f3577b34da6a3ce929d0e0e4736",
    "span_id": "00f067aa0ba902b7",
    "message": "Payment processed successfully",
    "feature_flag": {"name": "new-checkout-flow", "enabled": true}
  }