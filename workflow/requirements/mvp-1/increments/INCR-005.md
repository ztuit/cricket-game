# INCR-005 — Visual style spike
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** high
**Dependencies:** INCR-002
**Created:** 2026-08-20

---
## Purpose
Prototype two visual approaches with real cricket scenarios: (1) clean 2D top-down with colour-coded zones, (2) comic-book-inspired UI chrome with bold borders, halftone textures, character silhouettes. The human validates before committing to a rendering approach. This de-risks the biggest aesthetic decision in the product.

**Ubiquitous language terms involved:**
Field Placement, Fielder, Pitch, Surface Condition, Bowler, Character Card, Nickname

---
## Acceptance criteria
- [ ] Approach A (2D top-down): field oval with fielder dots, pitch strip with colour-coded zones, bowler info label, shot type buttons, scoreboard bar
- [ ] Approach B (comic book): bold borders, halftone textures, character silhouettes, dramatic colours, same functional elements
- [ ] Both approaches render a real cricket scenario: facing a delivery with field visible, surface conditions shown, shot selection available
- [ ] Both approaches run on a physical Android device
- [ ] Human reviews both approaches and makes a decision
- [ ] Decision documented with rationale
- [ ] GameRenderer interface confirmed or refined based on what the renderers actually need

---
## Technical notes
**SE:** Both approaches use Compose Canvas for the field/pitch rendering and Compose for UI chrome (buttons, scoreboard). The spike is NOT a full implementation — it is a prototype showing one delivery scenario. The GameRenderer interface (ADR-001) may need refinement after this spike: the renderers will reveal what data they actually need (e.g., ball trajectory vs just start/end positions). Build both as separate Composable functions that implement the same GameRenderer interface.

**Cyber:** None — this is a visual prototype, no data handling.

**UX:** This spike validates the two candidate visual styles. The outcome determines the rendering approach for the entire game. Key things to evaluate: (1) Can the player read the field at a glance? (2) Are surface conditions visible without cluttering? (3) Does the character personality come through? (4) Is the action zone (bottom) thumb-reachable and clear?

**Ops:** None — prototype only.

**DDD:** None — rendering concern only.

---
## Deployment validation
1. Build both visual approaches as separate Composable screens
2. Each approach renders a static cricket scenario: field, pitch, bowler info, shot selection, scoreboard
3. Install on physical device and verify both render without crash
4. Human reviews both and records decision
5. Document which approach is chosen and why
6. If GameRenderer interface was refined, update ADR-001
