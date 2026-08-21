---
name: retrospective
description: Defines how the team captures what was learned at the end of each MVP increment and produces an outcome document that permanently improves how all agents operate.
---

# Skill: Retrospective

> **Purpose:** Capture what the team learned at the end of each MVP increment and produce an outcome document that permanently improves how all agents operate.

---

## Retrospective documents location

```
workflow/retrospective/mvp-<N>-retro.md       ← input: agents write here during the MVP
workflow/retrospective/mvp-<N>-outcome.md     ← output: Delivery Lead produces this after
workflow/retrospective/outcome-current.md     ← symlink or copy of the latest outcome doc
                                                 read by ALL agents on every invocation
```

---

## Phase 1 — Continuous logging (during the MVP)

Throughout the MVP increment, any agent may append an item to `workflow/retrospective/mvp-<N>-retro.md` under the appropriate heading. Agents are encouraged to log items as they occur rather than trying to remember at the end.

### Retro log entry format

```markdown
## Good
<!-- Things that worked well and should be kept or repeated -->
- [agent-name] [date] Description of what worked well.

## Bad
<!-- Things that caused friction, delay, confusion, or rework -->
- [agent-name] [date] Description of what went wrong or was painful.

## Keep
<!-- Specific practices, processes, or decisions that should continue unchanged -->
- [agent-name] [date] Description of what should be kept.

## Change
<!-- Specific suggestions for what should be different next time -->
- [agent-name] [date] Description of the suggested change and the expected benefit.
```

---

## Phase 2 — Directed review (when Delivery Lead calls the retrospective)

The Delivery Lead invokes the retrospective at the end of each MVP increment after the gate has closed. Each agent is directed to review the retro log and comment on items relevant to their role.

### Agent commenting format

```markdown
### [agent-name] comment on: [original item]
[Date] [The agent's perspective, any additional context, or agreement/disagreement with the item.]
```

Agents comment only on items where they have direct experience or relevant perspective. They do not comment on every item.

---

## Phase 3 — Outcome document (Delivery Lead produces)

After all agents have commented, the Delivery Lead produces `workflow/retrospective/mvp-<N>-outcome.md` and updates `workflow/retrospective/outcome-current.md`.

### Outcome document format

```markdown
# Retrospective Outcome — MVP [N]

**Date:** YYYY-MM-DD
**Produced by:** Delivery Lead
**MVP ref:** [MVP identifier]

---

## Summary

[2–3 sentences describing the MVP and the overall tone of the retrospective.]

---

## What we are keeping

| Practice / decision | Rationale | Owner |
|---|---|---|
| [What] | [Why we're keeping it] | [Who ensures it continues] |

---

## What we are changing

| Change | Rationale | Owner | Applied from |
|---|---|---|---|
| [What changes] | [Why] | [Who owns the change] | MVP [N+1] |

---

## Open items

| Item | Owner | Due |
|---|---|---|
| [Unresolved item] | [Agent or human] | [MVP N+1 / date] |

---

## Instructions to all agents

> These instructions take effect immediately and apply to all future invocations until superseded by a later outcome document.

- [Specific actionable instruction derived from the retrospective]
- [Another instruction]

```

---

## How agents use the outcome document

Every agent reads `workflow/retrospective/outcome-current.md` at the start of every invocation, immediately after reading their own memory file and before reading any other context.

The "Instructions to all agents" section is treated as a standing operating instruction. If a new outcome document supersedes a previous instruction, the new one takes precedence. Agents note any conflict between instructions from different outcome documents in their memory file.
