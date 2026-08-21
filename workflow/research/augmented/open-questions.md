# Open Questions — Cricket Game
**Status:** published
**Date:** 2026-08-20

Prioritised list of questions the source material does not answer. Each is tagged with the downstream agent that most needs it resolved.

**Note:** 12 of the original 16 questions have been answered by the human (2026-08-20). See `knowledge-basis.md` for the synthesised answers. Remaining questions and new questions derived from the answers are below.

---

## High — affects domain modelling and product scope

### OQ-17: Which shot types should be included in the game?
**Question:** Research has identified ~18 standard cricket shots (drives, pulls, cuts, sweeps, unorthodox shots). The game needs a curated subset. Which shots are in, and how are they categorised for the UI?
**Why it matters:** Too many shots overwhelm the mobile UI under time pressure. Too few reduce cricket authenticity. The DDD Expert needs this to model the shot selection domain. The UX Expert needs it to design the selection UI.
**Agent needs it:** DDD Expert, UX Expert
**Suggested approach:** Product Owner / Human selects a set of ~8–12 shots covering the key categories (defensive, front-foot, back-foot, unorthodox). Consider grouping (e.g., "drive" as a category with sub-options for direction).

### OQ-18: What bowler types exist?
**Question:** The human said bowler types should come from research. Standard cricket bowler types include: fast, medium-fast, medium, off-spin, leg-spin, left-arm orthodox, left-arm wrist spin. Which are in the game?
**Why it matters:** Bowler type determines bowling characteristics, field sets, and the types of deliveries the player faces. Core domain entity.
**Agent needs it:** DDD Expert
**Suggested approach:** DDD Expert researches standard bowler types and proposes a set. Human confirms.

### OQ-19: What does "progressing their team" mean?
**Question:** The human said "as a player progresses their team they will face more difficult opponents." What is the team? How does it progress? Is there a league, a campaign, a player collection?
**Why it matters:** This is the meta-game — the structure around individual matches. It affects domain model (team entity, progression system), UX (menus, progression screens), and scope (MVP may not include full progression).
**Agent needs it:** DDD Expert, Product Owner, TPO
**Suggested approach:** Human describes the team progression concept — even a rough idea. Is it like a card game (collect players)? A league (advance through divisions)? A campaign (fixed sequence of matches)?

### OQ-20: How is the target score calculated?
**Question:** When the batsman wins the toss and "sets a target," how is the target determined? When chasing, how is the chase target "given at the start"? Is it fixed, random, or difficulty-based?
**Why it matters:** Affects match resolution logic and the game's difficulty model.
**Agent needs it:** DDD Expert, Coder
**Suggested approach:** Human clarifies: is the target a fixed number per match? Generated based on difficulty? Based on the AI bowling team's simulated innings?

### OQ-21: Is there a partnership model (two batsmen) or single batsman?
**Question:** In real cricket, two batsmen are on the field — the striker and the non-striker. Does this game model both, or does the player face every delivery as a single batsman?
**Why it matters:** Affects whether run-outs, running between wickets, and strike rotation are game mechanics. Simplifies or complexifies the domain model significantly.
**Agent needs it:** DDD Expert
**Suggested approach:** Human confirms: single batsman (simplified) or two batsmen (more realistic)?

---

## Medium — affects UX and technology

### OQ-22: What visual style should we prototype first?
**Question:** The human mentioned 2D top-down and comic book style as options. Which should the first prototype use? Or should we build a minimal prototype that works in either style?
**Why it matters:** This is the biggest technology decision. It determines the UI framework, rendering approach, and asset requirements.
**Agent needs it:** UX Expert, Senior Engineer
**Suggested approach:** UX Expert proposes two quick prototypes (one 2D top-down, one comic-book style) for the human to react to. Or: start with a minimal functional prototype and layer visual style on top.

### OQ-23: How does the freemium lock work?
**Question:** The human suggested "locked down to say one over unless a fee is paid." What exactly is locked? One over per session? Per day? Is it a one-time purchase or subscription?
**Why it matters:** Affects architecture (in-app purchase), game design (the free experience must be compelling), and scope (payment integration in MVP?).
**Agent needs it:** Product Owner, Senior Engineer
**Suggested approach:** Human confirms the freemium model. Even a rough idea helps: "free = 1 over per match, paid = full 20 overs" or similar.

### OQ-24: What screen sizes and device performance should we target?
**Question:** Android 12 is the minimum version, but what about screen sizes (small phone vs tablet) and device performance (low-end vs flagship)?
**Why it matters:** Affects rendering complexity, asset resolution, and UI layout strategy.
**Agent needs it:** Senior Engineer, UX Expert
**Suggested approach:** Human states: "phones only" or "phones and tablets." Performance: "must work on budget phones" or "flagship OK for v1."

---

## Low — can be deferred

### OQ-25: T20 bowling restrictions?
**Question:** In real T20 cricket, each bowler can bowl a maximum of 4 overs. Does this apply in the game? How many bowlers does the AI use?
**Why it matters:** Affects bowler rotation logic and match variety.
**Agent needs it:** DDD Expert
**Suggested approach:** DDD Expert proposes default (standard T20 rules). Human confirms or overrides.

### OQ-26: How does the AI batting team's innings work (when setting a target)?
**Question:** When the player bats first (sets a target), the AI team bats second. When the player bats second (chases), the AI batted first and set the target. How is the AI's innings simulated?
**Why it matters:** Affects whether there's a full AI batting simulation or just a target number provided.
**Agent needs it:** DDD Expert, Coder
**Suggested approach:** Simplest: the AI's score is pre-calculated or generated as a number. More complex: the AI bats in a simulated innings the player can watch. Human clarifies the intended experience.

---

## Resolved questions (from human response 2026-08-20)

| ID | Question | Answer summary |
|---|---|---|
| OQ-01 | Match structure | T20 format: 20 overs max, 6 balls/over, 11 players, 10 wickets. Toss decides target-setting vs chasing. |
| OQ-02 | Shot types | To be established from research — standard cricket shots identified. |
| OQ-03 | Win/loss conditions | Derived from OQ-01: reach target (win) or fail to reach it (lose). |
| OQ-04 | Multiplayer | Single player for this iteration. Multiplayer noted for future. |
| OQ-05 | Business model | Freemium: possibly 1 over free, pay for full match. TBD. |
| OQ-06 | Offline vs online | Offline for v1. Architect for future server/leaderboard/accounts. |
| OQ-07 | Visual style | Visually appealing and modern. 2D top-down or comic book. Not text. |
| OQ-08 | Difficulty progression | Match-level: coefficients change per delivery. Career-level: tougher opponents as team progresses. |
| OQ-09 | Bowler characteristics | Bowler type defines delivery. Experience class improves accuracy. "Think comic book" — character-driven. |
| OQ-10 | Wrist angle | Visual dial with degree rotation. |
| OQ-11 | Field placement | Predefined sets, selected by bowler type, adjusted as match progresses. |
| OQ-12 | Target audience geography | Global, UK based initially. |
| OQ-13 | Real vs fictional content | Real teams/grounds, fictional players with nicknames and quirks. |
| OQ-14 | Bowler stats display | Player can open a "card" for any player at any time. |
| OQ-15 | Surface coefficient model | Varies across pitch, changes per over, affected by weather, ball count, ball age. |
| OQ-16 | Android version | Android 12 (API 31) as base. |
