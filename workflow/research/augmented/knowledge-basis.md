# Knowledge Basis — Cricket Game
**Status:** published
**Date:** 2026-08-20
**Sources:** `workflow/research/opportunity/cricket-game.md`, human responses to open questions (2026-08-20)

---

## Product concept

**Known:** The product is a mobile game based on cricket. The core mechanic challenges the player to select the correct batting shot based on the delivery they receive and the field placement. (Source: `cricket-game.md`, lines 3–6)

**Known:** The game targets people who already understand cricket — it is not designed to teach cricket to non-fans. (Source: `cricket-game.md`, line 6; human confirmed: "Global, UK based initially")

**Known:** The game uses a T20-style format: 6 balls per over, maximum 20 overs, 11 players per side, 10 wickets maximum. The player always bats. If the toss is won, the batsman sets a target; if lost, the batsman chases a given target. (Source: human response to OQ-01)

**Known:** Real teams and grounds are used, but players are fictional with unique entertaining characteristics, nicknames, cricket skills, and non-cricket quirks. Example: "the golden duck" for a batsman who is very good but has a higher than average probability of getting out without scoring. (Source: human response to OQ-13)

---

## Core gameplay loop

**Known:** The gameplay loop per delivery is:

1. **Pre-delivery:** Player sees the field arrangement (from predefined sets, selected based on bowler type and match conditions) and bowler statistics. Player can open a "card" for any player at any time to see all details. Player signals readiness. (Source: `cricket-game.md`, lines 10–11; human response to OQ-11, OQ-14)
2. **Delivery display:** The ball's line, length, landing spot, pace, and limited spin information are shown. Surface coefficients affect bounce and vary across the pitch. (Source: `cricket-game.md`, lines 12–13; human response to OQ-15)
3. **Shot selection:** Player has a time limit (seconds) to choose a shot type from a menu and select a wrist angle via a visual dial (degree rotation). (Source: `cricket-game.md`, line 14; human response to OQ-10)
4. **Outcome resolution:** A probability calculation determines whether the ball is hit, where it goes, or whether the batsman is out. Factors include: shot suitability to the delivery, ball coefficients, surface coefficients, and player characteristics. (Source: `cricket-game.md`, lines 14–16)

**Known:** Specific example outcomes:
- Backfoot shot to a full ball on stumps → wicket (bowled). (Source: `cricket-game.md`, line 16)
- Backfoot shot to a spinner with good length → caught in slips if fielders are present. (Source: `cricket-game.md`, line 16)

**Known:** The outcome model is probabilistic. "Probability of hitting the ball" suggests a random element weighted by the inputs. (Source: `cricket-game.md`, line 14)

---

## Shot types

**Known:** The player can choose from standard cricket shots. Based on research (Wikipedia: Batting (cricket)), the orthodox shot set includes:

**Vertical-bat (straight-bat) shots:**
- Defensive shot (forward defensive, backward defensive)
- Leave (not playing the ball)
- Drive: straight drive, off drive, on drive, cover drive, square drive
- Leg glance / flick

**Horizontal-bat (cross-bat) shots:**
- Cut: square cut, late cut
- Pull (waist-high deliveries)
- Hook (chest-high deliveries)
- Sweep: paddle sweep, hard sweep

**Unorthodox shots (higher risk, higher reward):**
- Reverse sweep
- Slog / slog sweep
- Upper cut
- Switch hit
- Scoop / ramp / Dilscoop
- Helicopter shot

**Unknown:** Which of these shots will be included in the game. The full set may be too many for a mobile UI. The human said "this can be established from research" — the above list is the research result. The Product Owner / DDD Expert will need to decide which to include.

**Inferred:** The game likely needs a curated subset — perhaps 8–12 shots — balancing cricket authenticity with mobile UI usability. Too many options in a time-pressured selection screen would be overwhelming.

---

## Physics and environment model

**Known:** The ball delivery is characterised by:
- **Line** — horizontal trajectory (e.g., outside off, on stumps, leg side)
- **Length** — where the ball pitches (e.g., full, good length, short)
- **Pace** — speed of the delivery
- **Spin** — some spin information, deliberately limited to increase difficulty (Source: `cricket-game.md`, line 12)

**Known:** Surface conditions are complex and dynamic:
- Varies across the pitch — different lines and lengths have subtly different surface coefficients
- Changes per over
- Changes based on dryness (weather is a match characteristic)
- Changes based on number of balls delivered to that part of the pitch
- Changes based on age of the ball
(Source: human response to OQ-15)

**Known:** Weather conditions at specific grounds introduce variables, including dryness which affects the surface. (Source: `cricket-game.md`, line 18; human response to OQ-15)

**Inferred:** This is a more sophisticated physics model than initially apparent. The surface is not a single coefficient but a grid or map of coefficients that degrades based on multiple factors. This will need careful domain modelling.

---

## Bowler model

**Known:** Bowlers have "specific characteristics" that provide consistency across overs. Bowler type defines the type of bowling delivered. Accuracy, success rate, and % of wides/no-balls improve with the experience class of the bowler. (Source: `cricket-game.md`, line 18; human response to OQ-09)

**Known:** Bowlers have "character" — the human said "think comic book." Entertaining characteristics, nicknames, personality. (Source: human response to OQ-09)

**Known:** Field placement is from predefined sets, selected based on bowler type and adjusted as the match progresses to suit conditions. (Source: human response to OQ-11)

**Unknown:** What bowler types exist (e.g., fast, medium-fast, medium, off-spin, leg-spin, left-arm orthodox, left-arm wrist spin). The human said "this should come from research."

**Unknown:** How many bowlers can bowl in a match. In real T20 cricket, a bowler can bowl a maximum of 4 overs. The human did not specify whether this constraint applies.

---

## Player characters

**Known:** Both batsmen and bowlers have:
- Cricket skills (batting/bowling attributes)
- Non-cricket characteristics and quirks (entertainment value)
- Nicknames
(Source: human response to OQ-13)

**Known:** Players are fictional. Real teams and grounds are used, but not real players. (Source: human response to OQ-13)

**Inferred:** This is a character-driven game. The player roster is part of the entertainment, not just a stats system. This has significant UX implications — character cards, personality presentation, and the "fun" factor are first-class features.

---

## Difficulty progression

**Known:** Two levels of difficulty progression:
1. **Match-level:** As a specific match progresses, the weights/coefficients change so certain shots have higher probability of resulting in a wicket or being caught. The batsman must be mindful of changing probabilities throughout the game. (Source: human response to OQ-08)
2. **Career/team-level:** As the player progresses their team, they face more difficult opponents for matches. (Source: human response to OQ-08)

**Inferred:** There is a meta-game layer — the player has a "team" that progresses. This implies team management, player acquisition, or at least a progression system beyond individual matches.

**Unknown:** What "progressing their team" means — unlocking better players? Earning currency? Advancing through leagues?

---

## Presentation

**Known:** The game should be "visually appealing and modern." Options considered: 2D top-down, comic book style. The human said "we will have to try some things to see what works." (Source: human response to OQ-07)

**Known:** Definitely not text with diagrams. Visual presentation is mandatory. (Source: human response to OQ-07)

**Known:** Player information is presented via "cards" that can be opened at any time. (Source: human response to OQ-14)

**Inferred:** The comic book style aligns with the "character-driven" nature of the game (nicknames, quirks, entertaining characteristics). A comic book visual style would reinforce the personality of the fictional players.

**Unknown:** Specific visual style — needs prototyping. 2D top-down vs comic book vs hybrid is not decided.

---

## Platform and technical

**Known:** Android phones are the initial target. Android 12 (API level 31) as the minimum version. (Source: `cricket-game.md`, line 25; human response to OQ-16)

**Known:** This iteration focuses on offline play for simplicity. Architecture should support future server communication, accounts, leaderboards, and community features. (Source: human response to OQ-06)

**Unknown:** Specific screen size targets, performance requirements for lower-end devices.

---

## Business model

**Known:** Not yet decided. The human mentioned a possible freemium model: "locked down to say one over unless a fee is paid, maybe?" (Source: human response to OQ-05)

**Inferred:** Freemium is the likely direction. The free tier gives a taste (one over), and payment unlocks full matches. This affects architecture (in-app purchase integration) and game design (the free experience must be compelling enough to convert).

**Unknown:** Pricing, what exactly is locked/unlocked, whether there are other monetisation paths (ads, cosmetics, etc.).

---

## Match structure (detailed)

**Known:**
- Format: T20-style (20 overs maximum)
- Overs: 6 balls per over
- Players: 11 per side, 10 wickets maximum
- Toss: If won, batsman sets a target; if lost, chases a target given at the start
- Player perspective: Always batting
- Bowler may change after each over
(Source: human response to OQ-01)

**Unknown:** How the target is calculated when setting. Whether the AI bowling team's score is simulated or shown. How the chase target is "given at the start" — is it a fixed number or generated based on difficulty?

**Unknown:** Whether there is a concept of partnerships (two batsmen), run-outs, or whether the game simplifies to a single batsman facing deliveries.

---

## Summary of remaining knowledge gaps

| Gap | Impact | Priority |
|---|---|---|
| Which shot types to include in the game | DDD, UX — cannot design shot selection UI | High |
| Bowler types and their characteristics | DDD — affects bowler entity model | High |
| What "progressing their team" means | DDD, Product Owner — affects meta-game model | High |
| How the target score is calculated | DDD, Coder — affects match resolution logic | Medium |
| Partnership model (single batsman vs two) | DDD — affects match entity model | Medium |
| Visual style decision | UX, Senior Engineer — affects technology choice | High |
| Freemium lock mechanics | Product Owner, Senior Engineer — affects scope | Medium |
| T20 bowling restrictions (max 4 overs per bowler) | DDD — affects bowler rules | Low |
| Screen size and device performance targets | UX, Senior Engineer | Medium |
