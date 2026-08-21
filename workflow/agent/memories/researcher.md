## 2026-08-20 — Initial research synthesis: Cricket Game
**Type:** decision
**Context:** First invocation of Researcher agent on the cricket game opportunity.
**What happened:** Synthesised the single opportunity document (`cricket-game.md`) into six output documents. Identified 16 open questions, 3 critical (match structure, shot types, win conditions).
**Impact:** The opportunity document was thin — described the core mechanic well but omitted match structure, scoring, business model, and visual style. The three critical questions had to be answered before DDD Expert could model the domain.
**Status:** resolved — human provided detailed answers to all 16 questions.

## 2026-08-20 — Human responses to open questions
**Type:** instruction
**Context:** Researcher checkpoint — presented suggested reading and open questions to human.
**What happened:** Human answered all 16 questions with significant detail:
- Match structure: T20 format (20 overs, 10 wickets, toss decides target/chase)
- Shot types: "establish from research" — standard cricket shots identified from Wikipedia
- Single player for v1, architect for multiplayer later
- Freemium model (possibly 1 over free, pay for more) — not finalised
- Offline for v1, architect for server/leaderboard/accounts
- Visual: modern, appealing — 2D top-down or comic book (not decided, needs prototyping)
- Bowler types from research, with "comic book" character and personality
- Real teams/grounds, fictional players with nicknames and quirks
- Surface model is sophisticated: varies by pitch location, weather, ball count, ball age
- Android 12 (API 31) as minimum
- Global audience, UK initially

**Impact:** Resolved 12 of 16 original questions. Generated 10 new questions (OQ-17 through OQ-26). The game is more character-driven than initially apparent — fictional players with personality are a first-class feature. The surface physics model is more complex than typical mobile games.

**Status:** resolved — all original questions answered. New questions open for downstream agents.

## 2026-08-20 — Lesson: always dispatch via task tool, not just handoff text
**Type:** lesson
**Context:** Researcher completed all outputs but only printed a HANDOFF block without actually dispatching to the next agent.
**What happened:** AGENTS.md and workflow.md both state: "The handoff statement should be followed by using a task to invoke the required agent." I printed the handoff but did not invoke the task tool. The human had to correct me.
**Impact:** Every agent must use the task tool to dispatch to the next agent after producing the handoff block. Printing the handoff alone is insufficient — it's the workflow equivalent of writing a TODO and not doing it.
**Status:** resolved

## Key observations for future invocations
- The core mechanic (shot selection under time pressure with incomplete information) is the product's differentiator. It's more a tactical puzzle than a cricket simulation.
- The document says "bowled by batsmen" (line 18) — this is a typo, should be "bowlers." All downstream agents should treat it as "bowlers."
- The game targets existing cricket fans, not newcomers. UK-based initially, global audience.
- Character-driven design: nicknames, quirks, personality are first-class features, not afterthoughts.
- Surface physics model is ambitious — varies by pitch location, degrades over time based on weather, ball count, ball age.
- Visual style is the biggest unresolved technology decision. Needs prototyping.
- Freemium model is likely but details are TBD. "1 over free, pay for more" is the starting point.
- **Grill-me is a human-facing conversation, not an internal checklist.** Every assumption flagged for human confirmation must actually be presented to the human before proceeding.

## 2026-08-20 — Grill-me failure across the workflow
**Type:** lesson
**Context:** After the Researcher, multiple agents (PO, DDD Expert, SE, all SMEs) ran Grill-me internally but never directed questions to the human. The human had to interrupt and point this out.
**What happened:** The DDD Expert flagged 6 assumptions as "human confirms" but never presented them. The SE identified 4 ambiguities but resolved them internally. The UX Expert made "binding" decisions without human validation. All agents treated Grill-me as an internal checklist rather than a human-facing conversation.
**Impact:** The human answered 10 questions that should have been asked before the SME stage completed. Two UX "binding" decisions were overridden (bowler card timing) or deferred (wrist angle, surface conditions). The DDD model needed updates.
**Status:** resolved — human provided answers, documents updated.

## 2026-08-20 — Human answers to DDD/SE/UX Grill-me questions
**Type:** instruction
**Context:** Human interrupted workflow to answer 10 questions that should have been asked earlier.
**Key decisions:**
- A-1: Single batsman confirmed for MVP 1. Partnerships (YES/NO/WAIT calling) planned for later.
- A-2: 10 shot types confirmed with variations (front/backfoot defence, late/square cut).
- A-3: 4 bowler types confirmed.
- A-4: Standard T20 rules (max 4 overs per bowler) confirmed.
- A-5: AI target pre-calculated for MVP 1. Fielding side gameplay wanted in future, not MVP 1.
- A-6: Minimal team tracking in MVP 1 (games played, experience). Deeper in MVP 2/3.
- SE-1: Spike visual style options.
- SE-2: Comic book stills for MVP 1, animation may be needed later for running.
- UX-1: Wrist angle swipe needs trialling.
- UX-3: Surface condition display needs trialling.
- UX-4: Bowler card NOT visible during delivery countdown — only on tap of player icon outside delivery window.
**Status:** resolved — all assumptions confirmed or modified.
