---
name: grill-me
description: Mandatory ambiguity-elimination step run at the start of every agent's work and at every human-facing checkpoint — surfaces assumptions and contradictions before they get built in.
---

# Skill: Grill-me

> **Purpose:** Eliminate ambiguity before acting. This skill is not optional — it is a mandatory step at the start of every agent's work and at every human-facing checkpoint. Assumptions are the primary source of wasted work in this workflow. Grill-me exists to surface them before they are built in.

**Core principle:** Every agent is an expert in their own area. That expertise comes with a responsibility: when there is ambiguity in your domain, you ask the human to resolve it. You do not assume. You do not decide unilaterally. You do not ask another agent to decide on the human's behalf. The human is the ultimate authority — agents propose, the human disposes.

---

## Mandatory invocation points

Grill-me is **always** run at these points — not only when an agent happens to notice ambiguity:

| Point | Who runs it | What is being grilled | Who they ask |
|---|---|---|---|
| Researcher start | Researcher | The opportunity documents — are they complete enough to produce a knowledge basis? | Human prompter |
| Product Owner start | Product Owner | The research output and any customer feedback — is there enough to commit to a vision? | Human prompter |
| DDD Expert start | DDD Expert | The product vision — is the domain clear enough to model? Are the domain objects and boundaries unambiguous? | Human prompter |
| SE start | Senior Engineer | The domain model and product vision — are the technology choices and architecture clear? | Human prompter |
| Cyber/UX/Ops/PE/TE start | Each SME | The SE architecture and domain model — are their area-specific requirements clear? | Human prompter |
| TPO backlog creation | TPO | The full techsme output — are requirements specific enough to write tickets against? | Human prompter |
| **Increment proposal** | TPO / Delivery Lead | The proposed increment with the human — is this the right thing to build next? | Human prompter |
| Feature Owner start | Feature Owner | The ticket acceptance criteria — are they testable and unambiguous? | Human prompter |
| Coder start | Coder | The ticket technical notes — are they specific enough to implement against? | Human prompter |

**At each of these points, the agent must ask the human — not another agent — about decisions in their own area.** The human is the only one who can resolve questions about what is actually wanted.

At each of these points, the agent must explicitly ask itself: *"Do I have enough clarity to act without making assumptions?"* If the answer is no, Grill-me starts before any output is produced.

---

## When to additionally invoke this skill

Beyond the mandatory points, any agent invokes Grill-me when:

- A requirement contains ambiguous terms that could reasonably be interpreted in more than one way
- A decision is being made where the stated goal and the proposed approach are not clearly aligned
- Two or more inputs contradict each other
- The agent is about to produce output that will be difficult or costly to reverse
- A stakeholder assumption is embedded in a request but has not been validated

The agent does **not** invoke Grill-me to delay work, avoid a decision, or seek permission. It is a precision tool, not a stalling mechanism.

---

## Who gets grilled

**Primary rule: Every agent asks the human about decisions in their own area of expertise.**

The human is the ultimate authority on all decisions. Each agent is responsible for surfacing ambiguities in their own domain and asking the human to resolve them — not assuming, not deciding unilaterally, not asking another agent to decide on the human's behalf.

| Agent | Their area |
|---|---|
| Product Owner | Product vision, priority, scope |
| DDD Expert | Domain model, ubiquitous language, boundaries |
| Senior Engineer | Architecture, technology choices, NFRs |
| Cyber Expert | Security, threats, data protection |
| UX Expert | Interaction design, look and feel, accessibility |
| Ops Support | Observability, logging, alerting |
| Platform Engineer | Pipeline, environments, deployment |
| Test Engineer | Test platform, coverage, defects |
| Technical Product Owner | Backlog, increments, requirements |
| Feature Owner | Test coverage, acceptance criteria |
| Coder | Implementation approach |

**What they ask the human about:** Anything in their area where a material decision is being taken and there is ambiguity. There is no narrow list. If the agent is about to make a call that could go multiple ways and the choice matters, they ask the human. They interrogate until they have clarity — one question at a time, but as many rounds as needed. The agent does not stop asking until the ambiguity is resolved or the human explicitly says "you decide."

**Secondary rule: Agents may ask other agents about questions outside their own area.**

When an agent encounters ambiguity in another agent's domain, they may ask that agent directly — but only for questions that don't require human judgment. If the question involves a material decision that could go multiple ways and the choice matters, it must go to the human regardless of whose "area" it falls in.

| Question type | Direct to |
|---|---|
| Product intent, priority, scope | Human prompter |
| Technical approach, architecture | Human prompter (SE proposes, human confirms) |
| Domain language, object boundaries | Human prompter (DDD Expert proposes, human confirms) |
| Security requirements, risk tolerance | Human prompter (Cyber proposes, human confirms) |
| UX decisions, design direction | Human prompter (UX proposes, human confirms) |
| Operational priorities | Human prompter (Ops proposes, human confirms) |
| Ticket requirement clarity | Human prompter (TPO proposes, human confirms) |
| Test coverage intent | Human prompter (Feature Owner proposes, human confirms) |
| Implementation approach | Human prompter (Coder proposes, human confirms) |
| Cross-domain clarification (e.g. SE needs to understand a DDD boundary) | The owning agent (e.g. DDD Expert) |
| Non-material technical questions (e.g. naming convention, file structure) | The owning agent |

**The human is always the final authority on material decisions.** Agents propose, the human disposes.

---

## How to run Grill-me

### Step 1 — Identify the uncertainty

State clearly what is ambiguous, contradictory, or low-confidence. Be specific.

❌ "This is unclear."
✅ "The term 'user' appears to refer to both the end customer and the internal admin in different parts of the document — which is intended here?"

❌ "I'm not sure about the scope."
✅ "The roadmap lists payment processing in MVP 2, but the bluesky document describes it as core to the value proposition of MVP 1. Which is correct?"

### Step 2 — Form a question

Ask **one question at a time**. The question must be closed enough to get a usable answer, but open enough not to lead the respondent. Avoid compound questions.

Before asking: check whether the answer is already present in a document you have not yet read. If so, read it first.

### Step 3 — Evaluate the answer

After receiving an answer, check:
- Is it internally consistent with other known facts?
- Does it resolve the ambiguity completely, or does it introduce a new one?
- Is the answer consistent with answers given earlier in the conversation?

If inconsistency remains, return to Step 2 with a follow-up question that surfaces the contradiction explicitly.

### Step 4 — Repeat until convergence

Continue until:
- All identified ambiguities are resolved
- The agent has sufficient confidence to produce output
- A contradiction has been identified that cannot be resolved at this level and must be escalated

### Step 5 — Record the outcome

Document the questions asked, answers received, and resolutions in the relevant agent memory file or ticket note. This prevents the same ambiguity from recurring.

---

## Termination conditions

Grill-me ends when **all** of the following are true:

- [ ] No term in the requirement has two reasonable interpretations
- [ ] The proposed approach is consistent with the stated goal
- [ ] All inputs to the agent are mutually consistent
- [ ] The agent can state in one sentence what it is about to do and why
- [ ] No assumption is being carried forward — every uncertain point has been resolved by a human or authoritative agent

---

## What Grill-me does not do

- Does not make the decision on behalf of the stakeholder
- Does not assume an answer and proceed
- Does not ask more than one question per round
- Does not invoke on trivial stylistic choices
- Does not substitute for reading available documents — read first, grill second
- Does not allow an agent to decide unilaterally on decisions in their own area — the human must be asked

## What happens when Grill-me is skipped

If an agent produces output without running Grill-me at a mandatory point, the output is **invalid**. It must be reviewed by the human before any downstream agent uses it. The Delivery Lead or TPO should flag the gap and present the output to the human for retroactive review before proceeding.

This is not a suggestion — it is a hard gate. An agent that skips Grill-me has made assumptions that may be wrong. Those assumptions propagate through every downstream document, and the cost of fixing them grows with each step.

---

## Increment proposal grilling (special case)

Before any work increment begins, the TPO or Delivery Lead proposes it to the human prompter. This proposal is always accompanied by a Grill-me session. The purpose is not just to confirm the increment is wanted, but to surface any assumptions baked into the ticket before they are built.

Mandatory questions for every increment proposal:

1. "Is this the right thing to build next, or should something else take priority?"
2. "Are there any constraints or context about this increment that are not in the ticket?"
3. "Is the scope of this increment as you understand it — [brief description]?"
4. "Are there any edge cases or scenarios you expect to be handled that are not in the acceptance criteria?"

The human's answers are recorded on the ticket before work begins.

---

## Example

**Trigger:** The Product Owner's roadmap says "launch to all users in MVP 1" but the Senior Engineer's architecture doc says "MVP 1 is an internal pilot only."

**Grill-me question (to human prompter):** "The roadmap describes MVP 1 as a full user launch, but the architecture document treats it as an internal pilot. These are significantly different in scope and operational requirements. Which did you intend, and should the other document be updated to match?"

**After answer:** Record the decision in both documents. If it changes scope significantly, the TPO updates affected tickets.

---

## Increment slicing check (mandatory before any proposal)

Before proposing an increment to the human, the TPO runs this slicing check. If any answer is "no," the increment must be split before proposing.

**1. One observable thing test**
Write the increment as: *"A [user/system] can now [single behaviour]."*
Does the sentence contain "and" or "also"? → Split it.

**2. Single failure point test**
If this increment is deployed and breaks, will the cause be immediately obvious?
If two different concerns could independently cause failure → Split it.

**3. Behaviour not layer test**
Is this increment defined by a technical layer rather than a user-observable behaviour?
"Add the database schema" is a layer. "A customer can register with an email address" is a behaviour.
Layers are only acceptable as increments when the layer itself is the deployable deliverable (e.g. infrastructure setup in MVP 1).

**4. Happy path isolation test**
Does this increment include both the success case and error/edge case handling?
If yes → Split: happy path first, error handling next.

**5. Scope creep test**
Read the acceptance criteria. Do any of them describe something that could be deferred without breaking the core behaviour?
If yes → Move that criterion to a new increment.

---

## Direction change response (mandatory behaviour)

When the human changes direction at any point, no agent challenges it or frames it as contradictory. The correct response from any agent receiving a direction change is:

1. *"Understood. Let me work out what this means."*
2. Ask **one** clarifying question if the change is ambiguous — not to push back, but to understand the scope of the change
3. Identify what needs to change and communicate it to the relevant agent (TPO for backlog, PO for vision)

Framing a human direction change as "contradictory to the agreed plan," "inconsistent with the roadmap," or "outside the agreed scope" is a violation of the Delivery Principles. The plan exists to serve the human's goals — it is not a constraint on those goals.
