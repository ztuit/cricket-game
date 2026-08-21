---
name: workflow
description: The authoritative sequence of agent invocations, gates, feedback loops, and escalation rules for the whole delivery process. Every agent reads this to know where it sits in the process.
---

# Skill: Workflow

> **Purpose:** Define the authoritative sequence of agent invocations, the gates that must be passed before proceeding, and the rules for feedback loops and escalations. Every agent reads this to understand where they sit in the process and what is expected of them.

---

## Agent invocation model

Agents in this workflow cannot directly invoke each other. When an agent says it is
"invoking" or "handing off to" another agent, what it means is:

1. It has completed its own work and written its outputs
2. It is signalling what should happen next
3. A human or orchestrator will initiate the next agent

**The correct completion pattern for every agent is:**

```
[Do the work]
[Write the outputs to the correct paths]
[Update the relevant status field]

---
HANDOFF
Next agent: [agent name]
They should read: [specific file paths]
Their first action should be: [one sentence]
```

Agents do not say "I am now invoking X." They say "X should now be invoked." They
do not wait for permission to complete their own work. They do not split their work
into "here is what I plan to do — shall I proceed?" followed by doing it.
The handoff statement should be followed by using a task to invoke the required agent.

**Specifically forbidden patterns:**
- "Shall I now pass this to the Feature Owner?"
- "I will invoke the Delivery Lead — please confirm"
- "I am now handing off to the TPO" [then stopping without producing the handoff content]
- Summarising intended actions and waiting for approval before executing them
- Asking whether to proceed to the next step within a single agent's scope of work

**The only pauses are the four human checkpoints defined above.** Everything else is
work to be completed and reported, not proposed and approved.

### Human approval REQUIRED (blocking)
| Checkpoint | Who presents | What is decided |
|---|---|---|
| Increment proposal | TPO / Delivery Lead | Is this the right increment to build next? Is the scope correct? |
| MVP Gate | Delivery Lead coordinates | Is the MVP complete and ready to close? |
| Vision change | Product Owner | Does a significant change require human sign-off before proceeding? |
| Research suggested reading | Researcher | Human reviews and optionally enriches before research is finalised |

### Automatic transitions (no human approval needed)
All other agent-to-agent handoffs are automatic once the sending agent marks their output with the appropriate status. Agents do **not** ask the human for permission to proceed to the next step unless it is one of the four checkpoints above.

If an agent is unsure whether to proceed automatically, the answer is: **proceed, and note what you did.**

---

## Directory structure

```
workflow/
├── research/
│   ├── opportunity/             # Initial supporting docs provided by human
│   └── augmented/               # Researcher output
│       ├── knowledge-basis.md
│       ├── glossary.md
│       ├── stakeholder-map.md
│       ├── open-questions.md
│       ├── researcher-summary.md
│       └── suggested-reading.md   ← NEW: curated external sources for human review
├── product/
│   ├── bluesky/                 # Product Owner: vision document
│   └── roadmap/                 # Product Owner: MVP roadmap
├── techsme/
│   ├── ddd.md
│   ├── senior-engineer.md
│   ├── cyber.md
│   ├── ux.md
│   ├── ops.md
│   └── platform-engineer.md   ← Platform Engineer strategy: pipelines, environments, PVT, artifacts
├── requirements/
│   └── mvp-<N>/
│       ├── backlog.md            # TPO: ordered increment list + gate sign-offs
│       ├── increments/
│       │   └── <increment-id>.md  # Replaces "tickets" — includes deployment lifecycle
│       └── releases/
│           └── <increment-id>-<commit-sha>.md  ← NEW: release notes per deployed increment
├── agent/
│   ├── memories/
│   └── reviews/
└── retrospective/
```

---

## Key terminology change: tickets → increments

What were called "tickets" are now called **increments** to make explicit that each one must be deployed and validated in a running environment — not just code-complete. A ticket implies code. An increment implies a deployed, observable, validated change.

---

## Agent invocation sequence

### Stage 1 — Discovery

| Step | Agent | Trigger | Reads | Writes | Transition |
|---|---|---|---|---|---|
| 1.1 | **Researcher** | Human initiates project | `workflow/research/opportunity/` | `workflow/research/augmented/` incl. `suggested-reading.md` | ⚠️ HUMAN CHECKPOINT: present suggested reading, await any enrichment, then auto-proceed |
| 1.2 | **Customer** `mode: vision_review` | PO publishes bluesky | bluesky, roadmap, researcher-summary | `workflow/agent/reviews/customer-vision-<date>.md` | Automatic → PO |

**Researcher Grill-me:** Before producing output, the Researcher must run Grill-me on the opportunity documents. If they are insufficient to produce a reliable knowledge basis, specific gaps are presented to the human before proceeding.

### Stage 2 — Product

| Step | Agent | Trigger | Reads | Writes | Transition |
|---|---|---|---|---|---|
| 2.1 | **Product Owner** | Researcher complete, customer review received | `workflow/research/augmented/`, customer review | `workflow/product/bluesky/`, `workflow/product/roadmap/` | Automatic → DDD Expert |

**PO Grill-me:** Before committing to a vision, the PO must run Grill-me. Any unresolved open questions from `open-questions.md` that affect the vision must be directed to the human before the bluesky is marked published.

**Gate 2.1:** bluesky and roadmap marked `status: published`.

### Stage 3 — Domain Modelling

| Step | Agent | Trigger | Reads | Writes | Transition |
|---|---|---|---|---|---|
| 3.1 | **DDD Expert** `mode: initial_model` | PO publishes | bluesky, roadmap, research augmented | `workflow/techsme/ddd.md` | Automatic → Senior Engineer |

**DDD Grill-me:** If the product vision is ambiguous about what domain objects exist or how they relate, the DDD Expert surfaces specific questions to the PO (not the human directly) before modelling.

**Gate 3.1:** `ddd.md` marked `status: initial-complete`.

### Stage 4 — SME Review

| Step | Agent | Trigger | Reads | Writes | Transition |
|---|---|---|---|---|---|
| 4.1 | **Senior Engineer** | DDD complete | `ddd.md`, bluesky, roadmap | `workflow/techsme/senior-engineer.md` | Automatic → Cyber, UX, Ops, Platform Engineer in parallel |
| 4.2 | **Cyber Expert** | SE complete | `senior-engineer.md`, `ddd.md` | `workflow/techsme/cyber.md` | Automatic → PO Gate when all SMEs done |
| 4.3 | **UX Expert** | SE complete | `senior-engineer.md`, bluesky | `workflow/techsme/ux.md` | Automatic → PO Gate when all SMEs done |
| 4.4 | **Ops Support** | SE complete | `senior-engineer.md`, `cyber.md` | `workflow/techsme/ops.md` | Automatic → PO Gate when all SMEs done |
| 4.5 | **Platform Engineer** `mode: initial_review` | SE complete | `senior-engineer.md`, `ddd.md`, roadmap | `workflow/techsme/platform-engineer.md` | Automatic → PO Gate when all SMEs done |
| 4.6 | **Test Engineer** `mode: initial_review` | SE complete | `senior-engineer.md`, `ddd.md`, roadmap | `workflow/techsme/test-engineer.md` | Automatic → PO Gate when all SMEs done |

**SE Grill-me:** Before committing to technology choices or architectural decisions, the SE must run Grill-me on any ambiguities in the domain model or product vision that force premature architectural commitment.

SME feedback to SE is automatic. SE may update `senior-engineer.md` before summaries are finalised. No human checkpoint.

**Platform Engineer ↔ SE feedback:** Where the deployment strategy reveals architectural concerns (e.g. the architecture makes zero-downtime deployment impractical), the Platform Engineer routes feedback to the SE before finalising `platform-engineer.md`. The SE may update `senior-engineer.md` in response. This loop is automatic — no human checkpoint.

**Test Engineer ↔ SE feedback:** Where the domain model makes behaviour hard to isolate or observe in a test (e.g. aggregates with no seam for mocking an external dependency), the Test Engineer routes feedback to the SE before finalising `test-engineer.md`. This loop is automatic — no human checkpoint. Note: at this stage the Test Engineer is reviewing testability, not building anything — the platform itself is not built until Stage 7b, after Increment 1.

### Stage 5 — PO Gate

| Step | Agent | Trigger | Reads | Writes | Transition |
|---|---|---|---|---|---|
| 5.1 | **Product Owner** | All SME summaries complete | Plain-language summaries in all techsme docs | PO approval notes in each techsme doc | Automatic → TPO once all approved |

**Gate 5.1 — PO Approval Gate (blocking, but human-free):**

The PO either approves or raises a concern. Concerns route back to the SME automatically. Only if a concern requires a **vision change** does the PO bring it to the human prompter.

### Stage 5b — Pipeline build (MVP 1 and when new infrastructure is needed)

| Step | Agent | Trigger | Reads | Writes | Transition |
|---|---|---|---|---|---|
| 5b.1 | **Platform Engineer** `mode: pipeline_build` | All PO approvals recorded; TPO begins backlog | `platform-engineer.md`, `senior-engineer.md` | Pipeline increment definitions handed to TPO | TPO slots pipeline increments at top of MVP 1 backlog |

Pipeline increments are first-class backlog items. They go through the same human proposal checkpoint as feature increments. The Platform Engineer produces the increment definitions; the TPO owns prioritisation and the proposal process. Pipeline increments in MVP 1 are always highest priority — feature increments cannot be deployed without them.

### Stage 6 — Planning

| Step | Agent | Trigger | Reads | Writes | Transition |
|---|---|---|---|---|---|
| 6.1 | **Technical Product Owner** | All PO approvals recorded, pipeline increments received | All techsme docs, bluesky, roadmap, pipeline increment definitions | `workflow/requirements/mvp-<N>/backlog.md`, increment files | ⚠️ HUMAN CHECKPOINT per increment (see below) |

**TPO Grill-me:** Before writing any increment, the TPO runs Grill-me on the techsme outputs. Requirements that are not specific enough to be implemented and validated independently must be resolved before the increment is written.

**Gate 6.1:** backlog marked `status: ready-for-delivery`.

#### Increment proposal — human checkpoint

Before the Delivery Lead picks up **any** increment for delivery, the TPO presents it to the human with:

```markdown
## Increment Proposal — [INCREMENT-ID]

**What:** [One sentence description]
**Why now:** [Why this is the right next thing — dependency or value reason]
**Scope:** [Exactly what is included and what is not]
**Acceptance criteria summary:** [The 3–5 key things that must be true when done]
**Deployment validation:** [How we will know it is working in the deployed environment]
**Estimated complexity:** low / medium / high

Questions for you before we begin:
1. Is this the right thing to build next, or should something be deferred?
2. Is the scope as you understand it?
3. Are there edge cases or constraints not captured here?
4. Is there anything about this that should change?
```

The human's answers are recorded on the increment before work begins. If the human defers the increment, it is moved to a `deferred` status in the backlog and the next increment is proposed.

**This is the only human checkpoint in the delivery loop.** Once an increment is approved by the human, delivery proceeds automatically through Feature Owner → Coder → deployment → validation without further human interrupts.

### Stage 7 — Delivery (per increment)

Each increment follows this lifecycle independently. All transitions within Stage 7 are **automatic** — no human approval between steps.

```
open → proposed → human-approved → in-progress → code-complete
     → deployed → pe-signed-off → validated → done
```

| Step | Agent | Trigger | Reads | Writes | Transition |
|---|---|---|---|---|---|
| 7.1 | **Delivery Lead** | Human approves increment | Increment file | Status: `in-progress` | Automatic → Feature Owner |
| 7.2 | **Feature Owner** | DL assigns | Increment file, `ddd.md` | Test code committed, increment updated | Automatic → Coder |
| 7.3 | **Coder** | Feature Owner initial tests written | Increment file, tests, `ddd.md`, `senior-engineer.md` | Implementation committed | Automatic ↔ Feature Owner (iterate) |
| 7.4 | **Feature Owner ↔ Coder** | Iterative | Previous state | Tests and impl until all criteria met | Automatic when FO signs off → `code-complete` |
| 7.5 | **Coder** | FO sign-off | Increment file, `platform-engineer.md` | Deployment executed through pipeline; commit SHA recorded → `deployed` | Automatic → release note production |
| 7.6 | **Coder** | Deployment complete | Pipeline output | `workflow/requirements/mvp-<N>/releases/<increment-id>-<sha>.md` | Automatic → Platform Engineer |
| 7.7 | **Platform Engineer** `mode: deployment_signoff` | Release note created | Release note, pipeline run log, PVT results | PE sign-off appended to release note → `pe-signed-off` or `pe-rejected` | If approved → DL. If rejected → Coder resolves and redeploys from 7.5 |
| 7.8 | **Delivery Lead** | PE sign-off approved | Release note, PE sign-off, deployed environment | Deployment validation evidence on increment → `validated` | Automatic → TPO proposes next increment |

> **Hard gate — PE sign-off is mandatory for ALL increments.**
> The Coder invokes PE after every deployment — pipeline and feature alike.
> No increment proceeds to `validated` without `pe-signed-off` on the release note.
> This was a process gap in MVP 1 (12/21 release notes missing PE sign-off).
> See retrospective: `workflow/retrospective/outcome-current.md` — Action 1.

#### DL Validation Checklist (step 7.8)

The Delivery Lead must verify ALL items before marking any increment `validated`.
Any "no" = `validation-failed`, not `validated`. "Non-blocking" is not a valid
classification for an acceptance criterion failure.

```markdown
## DL Validation Checklist — [INCREMENT-ID]

### Pre-validation gates
- [ ] PE sign-off present on release note? (pe-signed-off)
- [ ] If pe-rejected: has Coder resolved and redeployed?
- [ ] Deploy workflow waited for CI completion? (verify timing)
- [ ] Deployed version matches expected commit SHA?

### Acceptance criteria verification
- [ ] All acceptance criteria verified against running code?
- [ ] "How to validate it works" steps executed against deployed environment?
- [ ] Health endpoint returns correct version?
- [ ] All PVT assertions pass?
- [ ] All endpoints return expected responses?

### Test coverage
- [ ] Tests exist for happy paths?
- [ ] Tests exist for unhappy paths?
- [ ] Tests exist for edge cases?
- [ ] All tests pass (local CI simulation)?
- [ ] GitHub Actions CI shows green?
- [ ] (Increment 2 onward, once the test platform exists) Coverage report reviewed — no unexplained drop?

### Architecture compliance
- [ ] Implementation consistent with techsme constraints?
- [ ] Ubiquitous language from ddd.md used correctly in code and tests?
- [ ] No new ADR violations?

### Observability
- [ ] Structured logging present for new functionality?
- [ ] Metrics exposed for new functionality?
- [ ] Correlation ID propagated?
```

**Hard rule:** If any item is "no," the increment is `validation-failed` or `pe-rejected` — not `validated`. No exceptions.
This checklist was added after MVP 1 where PIPELINE-008 was marked "validated" despite two AC failures.
See retrospective: `workflow/retrospective/outcome-current.md` — Action 2.

**DDD increment review trigger:** When an increment introduces new domain objects or schema changes, the TPO invokes DDD Expert `mode: increment_review` before `code-complete`. Automatic — no human checkpoint.

**PE rejection loop:** If the Platform Engineer rejects the deployment sign-off (`pe-rejected`), the Coder resolves the specific issue raised and redeploys from step 7.5. The PE re-reviews. This loop is automatic — no human checkpoint unless the issue is architectural, in which case the SE is consulted.

#### Increment status values

| Status | Meaning |
|---|---|
| `open` | In backlog, not yet proposed |
| `deferred` | Proposed to human and explicitly deferred |
| `proposed` | Presented to human, awaiting response |
| `human-approved` | Human confirmed to proceed |
| `in-progress` | Feature Owner / Coder actively working |
| `code-complete` | All tests pass; not yet deployed |
| `deployed` | Running in environment; commit SHA recorded; PVT run initiated |
| `pe-signed-off` | Platform Engineer confirmed pipeline, PVT, and observability healthy |
| `pe-rejected` | Platform Engineer rejected — Coder must resolve and redeploy |
| `validated` | DL verified acceptance criteria met in deployed environment |
| `done` | Complete; contributes to MVP gate |

### Stage 7b — Test platform build (once, after Increment 1 reaches `done`)

| Step | Agent | Trigger | Reads | Writes | Transition |
|---|---|---|---|---|---|
| 7b.1 | **Test Engineer** `mode: platform_build` | Increment 1 reaches `done` | Increment 1's actual test files, increment record, `test-engineer.md` | Test platform backlog items (`TEST-00x`) handed to TPO | TPO schedules them by real pain — not automatically as Increment 2 |

This deliberately happens *after* Increment 1, not before it, and is the one
place in this workflow where build order is inverted relative to Platform
Engineer's pipeline. Platform Engineer's pipeline must exist before Increment
1 can deploy at all — that's a hard dependency. The test platform has no such
hard dependency: Increment 1 can be tested ad hoc with whatever the
language's base test tooling provides, and doing so is what tells the Test
Engineer what the platform actually needs to support. Building it earlier
risks guessing wrong; building it later than this risks Feature Owner
re-inventing ad hoc setup on every subsequent increment.

`TEST-00x` items are first-class backlog items, proposed to the human like
`PIPELINE-00x` items, but they are **not** a hard gate — the TPO can slot
feature increments ahead of them if that's the right call. Automatic —
no human checkpoint on this trigger itself; the human checkpoint is the
normal increment proposal for whichever `TEST-00x` item the TPO schedules.

**Test Engineer defect and regression intake (ongoing, from Increment 1 onward):**
Whenever Feature Owner, Coder, or DL finds a test that reveals a genuine
defect — not just an incomplete feature, but something that's actually
wrong — it's logged to `workflow/quality/defects.md` and the Test Engineer
notifies whoever owns the fix (Coder for implementation defects, DL where
ownership is unclear). A **regression** — a previously-passing test now
failing without an intentional, documented behaviour change — is notified
immediately, not batched for the next retrospective. This applies whether
or not the formal test platform has been built yet.

### Stage 8 — MVP Gate

The MVP Gate is triggered when **all** increments in the MVP are `done` (status: `validated`). The Delivery Lead initiates it.

**Gate 8.1 — MVP Approval Gate (blocking):**

| Sign-off | Agent | What they review |
|---|---|---|
| `se-approved` | Senior Engineer | All release notes; architecture consistency; no unplanned drift |
| `cyber-approved` | Cyber Expert | All release notes; security requirements met; no open actions |
| `ux-approved` | UX Expert | All release notes; UX guidelines followed |
| `ops-approved` | Ops Support | All release notes; observability requirements met; runbooks present |
| `pe-mvp-approved` | Platform Engineer | All deployment sign-offs present; PVT suite coverage grown appropriately; pipeline healthy across all increments |
| `test-mvp-approved` | Test Engineer | Coverage trend healthy; all defects triaged (fixed or explicitly deferred); no unresolved regressions |
| `dl-approved` | Delivery Lead | All deployment evidence present; all validation criteria met; all pe-signed-off statuses confirmed |
| `po-approved` | Product Owner | MVP delivers intended stakeholder value; vision is satisfied |

Each reviewer reads the **release notes** for all increments in the MVP — not just the backlog or ticket list. The release notes are the evidence of what was actually built and deployed.

The Customer `mode: mvp_review` is invoked by the Delivery Lead as part of the gate. Customer feedback is assessed by TPO and PO before final closure.

A rejected sign-off produces a review document in `workflow/agent/reviews/` with specific actions. The TPO converts those to new increments. The MVP does not close until all sign-offs are `approved`.

⚠️ **Human checkpoint:** The human is informed when the MVP Gate is complete and the MVP closes. They do not sign off on individual increments within the gate — that is the agents' responsibility.

### Stage 9 — Retrospective

Automatic. Delivery Lead initiates after MVP closes. Each agent contributes. Delivery Lead produces outcome doc. All agents read it on next invocation. No human checkpoint unless the human wants to participate.

---

## Release notes format

Every deployed increment requires a release note at `workflow/requirements/mvp-<N>/releases/<increment-id>-<commit-sha>.md`:

```markdown
# Release Note — [INCREMENT-ID]

**Commit SHA:** [full SHA]
**Deployed:** YYYY-MM-DD HH:MM UTC
**Environment:** [staging / production-like / etc.]
**MVP:** [N]
**Deployed by:** Coder

---

## What was deployed

[Plain-language description of what this increment adds or changes]

## PVT assertions added in this increment

[What startup assertions were added, what each checks, and why it matters.
If no new assertions: state why — e.g. "no new dependencies or wiring in this increment."]

| Assertion | What it checks | Failure message |
|---|---|---|
| [name] | [what it verifies at startup] | [human-readable message if it fails] |

**Service started successfully:** yes / no
_(If no: deployment failed — see failure log. Release note should not exist yet.)_

## How to observe it is running

[Specific: what log entries, metrics, or health endpoints confirm the deployment is live]
- Log entry: `[example log line with field names]`
- Metric: `[metric name]` should show `[expected value or trend]`
- Health check: `[endpoint or command]` should return `[expected response]`

## How to validate it works

[Step-by-step validation that a human or the DL can execute against the deployed environment]
1. [Step]
2. [Step]

## How to validate it fails correctly

[Step-by-step verification that error cases behave as specified]
1. [Step]

## Rollback procedure

[How to undo this deployment if something is wrong]
1. [Step]

## Known limitations

[Anything deliberately not included in this increment that a tester might expect]

## Deployment evidence

[Links, screenshots, or log excerpts confirming successful deployment]
```

---

## Feedback loops

| Loop | From | To | Trigger | Automatic? |
|---|---|---|---|---|
| SME → SE | Cyber / UX / Ops | Senior Engineer | Architectural concern | Yes |
| SME → TPO | Any SME | Technical PO | New constraint | Yes |
| PO concern → SME | Product Owner | Relevant SME | Vision impact | Yes |
| PO vision change → human | Product Owner | Human prompter | Significant vision change | ⚠️ Human |
| DL → TPO | Delivery Lead | Technical PO | Requirement problem | Yes |
| Gate fail → TPO | MVP Gate | Technical PO | Sign-off rejected | Yes |
| Customer → PO | Customer | Product Owner | Vision review / reprioritise | Yes |
| Customer → TPO | Customer | Technical PO | MVP feedback needs backlog change | Yes |
| TPO → PO | Technical PO | Product Owner | Direction change concern | Yes (PO escalates to human if needed) |
| DDD review → TPO | DDD Expert | Technical PO | Increment review complete | Yes |
| PE → SE | Platform Engineer | Senior Engineer | Deployment strategy reveals architectural concern | Yes |
| PE rejection → Coder | Platform Engineer | Coder | PVT failed or pipeline issue | Yes |
| PE → TPO (pipeline increments) | Platform Engineer | Technical PO | Pipeline increment definitions produced | Yes |
| Test Engineer → SE | Test Engineer | Senior Engineer | Domain model hard to isolate/observe in a test | Yes |
| Test Engineer → TPO (platform increments) | Test Engineer | Technical PO | Increment 1 done — platform backlog items produced | Yes |
| Test Engineer → Coder (defect) | Test Engineer | Coder | Defect or regression found, ownership clear | Yes |
| Test Engineer → DL (defect) | Test Engineer | Delivery Lead | Defect found, ownership unclear | Yes |
| FO → Test Engineer | Feature Owner | Test Engineer | Test platform gap blocks writing a needed test | Yes |
| Retro → all | DL | All agents | Outcome doc published | Yes |
| Increment deferred → next | Human | TPO / DL | Human defers proposed increment | ⚠️ Human |

---

## Escalation authority

| Decision | Authority |
|---|---|
| Product direction change | Human prompter (via PO) |
| Increment deferral | Human prompter |
| Backlog prioritisation | Technical Product Owner |
| Increment requirement clarification | Technical Product Owner |
| Architecture decision | Senior Engineer |
| Security requirement | Cyber Expert (cannot be waived unilaterally) |
| UX guideline exception | UX Expert |
| MVP gate sign-off | Each agent for their own domain |
| Retrospective outcome | Delivery Lead |

---

## Customer invocation log

| Date | Mode | Triggered by | Document refs | Version | Output ref |
|---|---|---|---|---|---|
| YYYY-MM-DD | vision_review | product-owner | workflow/product/bluesky/v1.md | v1 | workflow/agent/reviews/customer-vision-YYYY-MM-DD.md |

### Invocation parameter schema

```yaml
mode: vision_review | vision_change | mvp_review
triggered_by: <agent-name>
document_refs:
  - <path>
version: <version or commit ref>
previous_version: <required for vision_change>
mvp_ref: <required for mvp_review>
```

---

## Document status values

| Status | Meaning |
|---|---|
| `draft` | Being worked on |
| `published` | Complete, available to downstream agents |
| `approved` | Required sign-off(s) received |
| `concern-raised` | Blocked pending resolution |
| `open` | Increment in backlog, not proposed |
| `deferred` | Explicitly deferred by human |
| `proposed` | Presented to human, awaiting response |
| `human-approved` | Human confirmed |
| `in-progress` | Actively being worked |
| `code-complete` | Tests pass, not yet deployed |
| `deployed` | Running in environment; PVT initiated |
| `pe-signed-off` | Platform Engineer confirmed pipeline, PVT, and observability healthy |
| `pe-rejected` | Platform Engineer rejected — Coder must resolve and redeploy |
| `validated` | DL verified acceptance criteria met in deployed environment |
| `done` | Complete, contributes to MVP gate |
| `rejected` | Sign-off refused; actions required |
| `closed` | MVP complete, all sign-offs received |
