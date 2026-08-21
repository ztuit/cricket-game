---
name: memory
description: Defines how each agent reads and writes its own persistent memory file across invocations — decisions, lessons, preferences, and open questions that should not be relearned each session.
---

# Skill: Memory

> **Purpose:** Give each agent persistent context across invocations. Memory is read at the start of every invocation and written at the end when something worth remembering has occurred.

---

## Memory file locations

Each agent has its own memory file:

```
workflow/agent/memories/researcher.md
workflow/agent/memories/product-owner.md
workflow/agent/memories/ddd-expert.md
workflow/agent/memories/senior-engineer.md
workflow/agent/memories/cyber-expert.md
workflow/agent/memories/ux-expert.md
workflow/agent/memories/ops-support.md
workflow/agent/memories/platform-engineer.md
workflow/agent/memories/technical-product-owner.md
workflow/agent/memories/test-engineer.md
workflow/agent/memories/delivery-lead.md
workflow/agent/memories/feature-owner.md
workflow/agent/memories/coder.md
workflow/agent/memories/customer.md
```

---

## On invocation — READ

At the start of every invocation, the agent reads its own memory file before doing anything else. The memory file provides:

- Decisions made in previous sessions that still stand
- Things that went wrong and how they were resolved
- Preferences or patterns that have emerged for this project
- Open questions that were not resolved in the last session
- Any explicit instructions recorded by a human or another agent

If no memory file exists yet, the agent notes this and proceeds — it will be created at the end of the first session.

---

## During invocation — NOTICE

The agent notices and flags for writing:

- A decision that was hard to reach and should not be revisited without good reason
- An approach that worked well and should be repeated
- An approach that failed and should be avoided
- A misunderstanding that caused rework
- An assumption that turned out to be wrong
- An explicit instruction from a human: "always do X" or "never do Y"

---

## On completion — WRITE

At the end of an invocation, the agent appends to its memory file when any of the following occurred:

- A significant decision was made
- Something went wrong or unexpectedly well
- A human explicitly asked for something to be remembered
- An open question was left unresolved

### Memory entry format

```markdown
## [YYYY-MM-DD] [Brief title]

**Type:** decision | lesson | preference | open-question | instruction

**Context:** One or two sentences describing what was happening.

**What happened / was decided:** The specific fact, decision, or lesson.

**Impact:** Why this matters for future invocations.

**Status:** resolved | open | superseded
```

---

## Memory hygiene

- Entries are **appended**, never edited or deleted (to preserve history)
- If a previous entry is superseded, add a new entry noting the change and marking the old one as `superseded`
- Memory files are **not** shared between agents — each agent reads only its own
- Memory files **are** visible to humans and can be edited by humans directly
- If a memory entry conflicts with a current instruction, raise it explicitly rather than silently ignoring either

---

## What memory is NOT

- It is not a full conversation log
- It is not a substitute for proper documentation in `workflow/`
- It is not a place for speculative notes — only things that actually happened
- It does not replace the retrospective — the retrospective is team-level, memory is agent-level
