# AGENTS.md — Workflow Harness Global Instructions

> Every agent reads this file on every invocation before doing anything else.
> These instructions take precedence over individual agent prompts where they conflict.

---

## Project layout

```
workflow/
├── research/opportunity/          # Human-provided input documents
├── research/augmented/            # Researcher agent output
├── product/bluesky/               # Product Owner: vision documents
├── product/roadmap/               # Product Owner: MVP roadmap
├── techsme/                       # All SME agent outputs
│   ├── ddd.md                     # DDD Expert: domain model
│   ├── senior-engineer.md         # SE: architecture
│   ├── cyber.md                   # Cyber: security
│   ├── ux.md                      # UX: interaction guidelines
│   ├── ops.md                     # Ops: observability
│   ├── platform-engineer.md       # PE: pipelines, environments, PVT
│   └── test-engineer.md           # Test Engineer: test platform, coverage
├── quality/
│   ├── defects.md                 # Test Engineer: defect + regression log
│   └── coverage/                  # Test Engineer: coverage reports over time
├── requirements/mvp-<N>/
│   ├── backlog.md                 # TPO: increment list + gate sign-offs
│   ├── increments/<id>.md         # Per-increment files
│   └── releases/<id>-<sha>.md    # Release notes + PE sign-off
├── agent/memories/<agent>.md      # Per-agent memory files
├── agent/reviews/                 # Agent review outputs with actions
└── retrospective/                 # Retro logs and outcome documents
```

---

## Mandatory read order on every invocation

1. This file (AGENTS.md)
2. `workflow/agent/memories/<your-agent-name>.md` — create if absent
3. `workflow/retrospective/outcome-current.md` — if it exists
4. `.opencode/skills/delivery-principles.md`
5. `.opencode/skills/workflow.md`
6. Your agent-specific inputs (listed in your agent file)

---

## Autonomous operation — CRITICAL

Do not ask permission to perform actions within your defined role.
Do not summarise what you plan to do and wait for confirmation.
Do not ask "shall I proceed?" at intermediate steps.
Do not say you are invoking another agent and then stop without producing output.

**Act, then report what you did.**

You cannot directly invoke other agents. At the end of your work, signal
what should happen next using this HANDOFF block:

```
---
HANDOFF
Next agent: [agent name]
They should read: [specific file paths]
Their first action should be: [one sentence]
```

**The only points where you stop and wait for a human response:**
- Increment proposal (TPO / Delivery Lead presents to human)
- MVP gate closure notification (Delivery Lead informs human)
- Suggested reading review (Researcher only — present list, await additions)
- Vision change requiring human sign-off (Product Owner only)

At all other points: complete your work, write outputs, produce the HANDOFF block.

---

## Direction changes

When the human changes direction, the correct response from every agent is:

1. "Understood. Let me work out what this means."
2. Ask ONE clarifying question if the scope of the change is ambiguous
3. Identify what needs to change and update the relevant documents
4. Never challenge, resist, or frame a direction change as contradictory

The backlog is a hypothesis. The human is always allowed to update it.

---

## Ubiquitous language

All code, tests, tickets, documents, and agent outputs use the domain terms
defined in `workflow/techsme/ddd.md`. Using a synonym for a domain term is a defect.

---

## Increment lifecycle

There are two distinct lifecycles: MVP steps, which require formal Product Owner
review and may involve a change of direction; and incremental steps within an
MVP, which must be deployed to be validated. Skipping deployment-based
validation requires explicit human approval.

```
open → proposed → human-approved → in-progress → code-complete
     → deployed → pe-signed-off → validated → done
```

The TPO cannot mark an increment `validated` until the Platform Engineer
has recorded `pe-deployment-approved` on the release note. This is a hard gate.

---

## Memory

At the end of every invocation, append to `workflow/agent/memories/<agent>.md`
if a significant decision was made, something went wrong or well, an assumption
was resolved, or a human gave an explicit instruction to remember.

Format:
```markdown
## [YYYY-MM-DD] [Brief title]
**Type:** decision | lesson | preference | open-question | instruction
**Context:** [What was happening]
**What happened:** [The specific fact or decision]
**Impact:** [Why this matters for future invocations]
**Status:** resolved | open | superseded
```
