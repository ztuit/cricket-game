# DDD Expert Memory

## [2026-08-20] Initial domain model produced
**Type:** decision
**Context:** First invocation of DDD Expert. Produced initial domain model for Cricket Game from bluesky v1, roadmap v1, knowledge basis, and glossary.
**What happened:** Produced `workflow/techsme/ddd.md` with:
- 6 bounded contexts (Match, Delivery, Player, Field, Pitch, Team)
- 5 aggregates (Match, Bowler, Batsman, Pitch, Field Placement)
- 10 value objects
- 16 domain events
- Draft schemas for all key entities
- 6 assumptions requiring human confirmation (OQ-17, OQ-18, OQ-20, OQ-21, OQ-25, OQ-19)
**Impact:** All downstream agents (SE, Cyber, UX, Ops, PE, Test Engineer) depend on this model. Assumptions A-1 through A-6 need human confirmation before increments can be designed.
**Status:** open — awaiting human confirmation on assumptions

## Key design decisions
- **Single batsman model (A-1):** Simplifies MVP 1. Customer flagged partnership as a missing scenario. If human wants partnerships, Match aggregate needs significant rework (non-striker entity, strike rotation, run-outs).
- **"Delivery" not "Ball":** Avoids ambiguity between the physical ball object and the event of bowling.
- **"Surface Condition" not "Coefficient":** Customer explicitly flagged "coefficients shift" as jargon. Domain model uses player-facing terms.
- **"Character Card" not "Card":** More specific. "Card" alone is too generic.
- **Ground vs Pitch:** Resolved the ambiguity flagged in the Researcher glossary. Ground = venue; Pitch = the strip where the ball bounces.
