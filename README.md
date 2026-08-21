# OpenCode Workflow Harness

A complete multi-agent development workflow for OpenCode (SST/opencode or Crush).

## Installation

Unzip this archive into the root of your project:

```
your-project/
├── AGENTS.md                   ← global instructions, read by all agents
├── README.md                   ← this file
├── .opencode/
│   ├── opencode.json           ← provider config, model settings, permissions
│   ├── agent/                  ← 14 specialised agent definitions
│   ├── command/                ← utility commands (handoff nudges, MVP cycle prompts)
│   └── skills/                 ← shared skills (delivery principles, workflow, etc.)
└── workflow/                   ← working directory created by agents at runtime
    ├── research/               ← put your opportunity docs in research/opportunity/
    ├── product/
    ├── techsme/
    ├── requirements/
    ├── agent/
    └── retrospective/

```

## Setup

1. Copy your API key into `opencode.json` or set the `ANTHROPIC_API_KEY` environment variable
2. Place your opportunity documents (problem statements, research, briefs) in `workflow/research/opportunity/`
3. Run opencode in your project root

## Workflow — agent sequence

Agents cannot invoke each other directly. Each agent completes its work, writes
its outputs, and ends with a `HANDOFF` block naming the next agent and what they
should read. You (or an orchestrator) dispatch the named agent next via `@agent-name`.
The full authoritative sequence, gates, and feedback loops are defined in
`.opencode/skills/workflow.md` — start there, not here, for the exact order.

```
@researcher               synthesises opportunity docs
@customer                 reviews the vision            (mode: vision_review)
@product-owner            produces bluesky + roadmap
@ddd-expert                produces initial domain model
@senior-engineer, @cyber-expert, @ux-expert, @ops-support, @platform-engineer,
@test-engineer             review in parallel           (test-engineer mode: initial_review)
@product-owner            approves SME summaries (PO Gate)
@platform-engineer        produces pipeline increments (mode: pipeline_build)
@technical-product-owner  creates MVP 1 backlog, proposes each increment to human
@delivery-lead            briefs Feature Owner and Coder per approved increment
@feature-owner            writes tests
@coder                    implements, deploys, writes release notes
@platform-engineer        signs off deployment (mode: deployment_signoff)
@delivery-lead            validates in the deployed environment
--- Increment 1 reaches done: @test-engineer builds the test platform (mode: platform_build) ---
--- repeat propose → validate for each increment ---
@delivery-lead            coordinates MVP gate; @customer reviews the MVP
@delivery-lead            runs the retrospective, produces the outcome doc
--- next MVP begins with the technical-product-owner proposing the next increment ---
```

A handful of one-off utility commands live in `.opencode/command/` (e.g. a
reminder to use proper task delegation on handoff) — these support the flow
above but are not the workflow itself.

## Agents

| Agent | Role |
|---|---|
| `@researcher` | Synthesises opportunity documents into knowledge basis |
| `@product-owner` | Produces product vision, roadmap, and reviews SME summaries |
| `@ddd-expert` | Domain model, ubiquitous language, bounded contexts, schemas |
| `@senior-engineer` | Architecture, technology choices, NFRs |
| `@cyber-expert` | Threat model, security requirements, mitigations |
| `@ux-expert` | Interaction technology, look and feel, UX guidelines |
| `@ops-support` | Observability, logging, alerting, runbook requirements |
| `@platform-engineer` | Pipeline, environments, PVT hook, deployment sign-offs |
| `@test-engineer` | Test platform, coverage reporting, defect and regression tracking |
| `@technical-product-owner` | Backlog, increment slicing, proposals, MVP gate tracking |
| `@delivery-lead` | Delivery coordination, validation, MVP gate, retrospectives |
| `@feature-owner` | Functional test authorship (happy/unhappy/edge paths) |
| `@coder` | Implementation, PVT assertions, deployment, release notes |
| `@customer` | Domain user reviews: vision, vision changes, MVP increments |

## Key principles

- Agents proceed autonomously within their role — they do not ask permission at each step
- Human checkpoints are limited to four: increment proposal, MVP gate, suggested reading, vision changes
- Every increment follows: proposed → human-approved → in-progress → code-complete → deployed → pe-signed-off → validated → done
- PVT assertions (written by the Coder) run at service startup — the service will not start if they fail
- The backlog is a hypothesis, not a contract — direction changes are welcomed, not challenged

## Requirements

- OpenCode (SST) v1.x or Crush v0.7x+
- Anthropic API key (or configure another provider in opencode.json)
