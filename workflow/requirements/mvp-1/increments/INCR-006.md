# INCR-006 — Field placement and pitch condition display
**MVP:** 1
**Status:** open
**Priority:** must
**Complexity:** medium
**Dependencies:** INCR-002
**Created:** 2026-08-20

---
## Purpose
Display the field placement (top-down diagram with 11 fielder positions) and pitch surface conditions (colour-coded zones) to the player during the delivery decision window. This is the tactical intel the player needs to make an informed shot selection.

**Ubiquitous language terms involved:**
Field Placement, Fielder, Pitch, Surface Condition, Bowler, Line, Length

---
## Acceptance criteria
- [ ] Top-down field view showing 11 fielder positions as dots on a green oval
- [ ] Fielders coloured by zone: close catchers (red), inner ring (amber), boundary (blue)
- [ ] Pitch strip divided into 3 zones: near batsman, middle, far
- [ ] Each pitch zone has colour overlay: Green (good), Amber (wearing), Red (degraded)
- [ ] Surface condition visible during delivery decision window
- [ ] Delivery information shown after ball is bowled: line, length, pace indicator (short text)
- [ ] Scoreboard bar always visible: score, overs, wickets, target
- [ ] Touch targets at least 48dp for all interactive elements

---
## Technical notes
**SE:** Compose Canvas renders the field oval and pitch strip. Fielder positions are data-driven from FieldPlacement value object. SurfaceCondition zones rendered as colour overlays on the pitch strip. The GameRenderer interface provides FieldPlacement and SurfaceCondition data — the renderer decides how to present it.

**Cyber:** None — display only.

**UX:** Field diagram is the primary tactical communication (UX-004). Surface condition colours must be distinguishable by luminance, not just hue (colour-blind accessibility). Delivery info uses cricket terms: "Short, outside off, fast" (UX-007). Scoreboard must never be hidden by overlays (UX-006).

**Ops:** None — display only.

**DDD:** Field Placement (11 FielderPositions) and SurfaceCondition (zone grid) are the domain objects rendered here. The display maps directly to the value objects defined in ddd.md.

---
## Deployment validation
1. Run on physical device: field diagram displays 11 fielder dots on green oval
2. Verify fielders are coloured by zone (close catchers red, boundary blue)
3. Verify pitch shows 3 zones with colour coding
4. Verify surface condition colours change when degradation values change
5. Verify scoreboard bar is visible and not obscured
6. Verify delivery info text displays correctly after a ball
7. Verify all touch targets are at least 48dp
