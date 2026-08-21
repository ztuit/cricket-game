# Researcher Summary — Cricket Game
**Date:** 2026-08-20
**Updated:** After human responses to open questions

---

## What this is

A mobile game for cricket fans. The player bats in a T20 cricket match — facing deliveries from AI bowlers and choosing the right shot based on the ball's line, length, pace, spin, and the field placement, all under time pressure. The core appeal is testing real cricket knowledge: knowing when to play a cover drive versus a pull shot, and understanding what the field is telling you about the bowler's plan.

## How it works

**Match format:** T20 — 20 overs maximum, 6 balls per over, 11 players per side, 10 wickets. If the player wins the toss, they set a target; if they lose, they chase one. The player always bats.

**Per delivery:**
1. See the field arrangement and bowler stats (or open the bowler's "card" for full details)
2. The ball is bowled — see where it will land, its pace, and limited spin info
3. Choose a shot type and wrist angle (visual dial) within a time limit
4. A probability model determines the outcome — hit, miss, boundary, or wicket

**What makes it interesting:**
- Surface conditions vary across the pitch and change over time (weather, ball age, wear)
- Bowlers have personality — fictional characters with nicknames, quirks, and comic-book style
- Real teams and grounds, but fictional players with entertaining characteristics
- Difficulty increases within a match (coefficients shift) and across the career (tougher opponents)

## What we now know (resolved)

| Question | Answer |
|---|---|
| Match structure | T20 format, 20 overs, 10 wickets, toss decides target/chase |
| Shot types | Standard cricket shots (research identified ~18, subset TBD) |
| Win/loss | Reach target = win; fail = lose |
| Multiplayer | Single player for v1 |
| Business model | Freemium (possibly 1 over free, pay for more) — TBD |
| Offline/online | Offline for v1, architect for future server features |
| Visual style | Modern, appealing — 2D top-down or comic book (not decided) |
| Difficulty | Match-level (coefficients change) + career-level (tougher opponents) |
| Bowler model | Bowler type + experience class + personality/character |
| Wrist angle | Visual dial with degree rotation |
| Field placement | Predefined sets, selected by bowler type and conditions |
| Players | Real teams/grounds, fictional players with nicknames and quirks |
| Player info | Card-based, accessible anytime |
| Surface model | Complex — varies by pitch location, weather, ball count, ball age |
| Android version | Android 12 (API 31) minimum |
| Target market | Global, UK initially |

## What we still don't know

| Gap | Why it matters |
|---|---|
| Which shots to include (~18 is too many for mobile UI) | UX design, domain model |
| Bowler types (fast, spin, etc.) | Domain model |
| Team progression system | Meta-game scope, domain model |
| Target score calculation | Match logic |
| Partnership model (1 or 2 batsmen) | Domain model |
| Visual style decision | Technology choice |
| Freemium lock details | Scope, architecture |
| Screen size / device targets | Technology, UX |

## My assessment

The concept has evolved significantly from the initial one-page idea. This is now a character-driven cricket strategy game with:
- A sophisticated surface physics model (varying by pitch location, degrading over time)
- Fictional players with personality (nicknames, quirks, comic-book style)
- Real cricket teams and grounds for authenticity
- A freemium model (1 over free, pay for full matches)
- T20 format as the match structure

The remaining gaps are important but not blocking. The DDD Expert can begin modelling the domain with what we know — the shot type and bowler type questions can be resolved during domain modelling. The visual style question is the biggest technology decision and should be resolved before the Senior Engineer commits to a stack.

## Recommendation

Proceed to Product Owner. The knowledge basis is strong enough for a vision document. The 8 remaining open questions are listed in `open-questions.md` and can be resolved during domain modelling and SME review — they don't block the PO from defining the product vision.
