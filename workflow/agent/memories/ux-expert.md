## 2026-08-20 Initial UX guidelines produced
**Type:** decision
**Context:** Initial review after Senior Engineer completed architecture. Customer vision review flagged key UX concerns: wrist angle interaction, shot selection under time pressure, "coefficients" jargon, bowler character card as tactical intel, visual style undecided.
**What happened:** Produced `workflow/techsme/ux.md` with binding interaction technology decisions (Jetpack Compose, Compose Canvas, Compose gesture APIs, WCAG 2.1 AA, Android 12+ phone-first portrait). Designed swipe-to-aim wrist angle model (not rotary dial). Designed two-tier shot selection (3 categories → 3-4 shots). Recommended comic book visual style with phased approach (placeholder → spike → commit). Documented all naming decisions where UI diverges from ubiquitous language.
**Impact:** Binding decisions on interaction model and accessibility standard. The swipe-to-aim and grouped shot selection are the core UX hypotheses that need playtesting validation. Visual style spike is a must-have early in MVP 1.
**Status:** open — awaiting PO approval and human playtest validation

## Key UX decisions made
- Wrist angle: swipe-to-aim on 5-zone arc, NOT rotary dial (Customer concern addressed)
- Shot selection: two-tier grouping (Front Foot / Back Foot / Unorthodox → specific shots)
- Timer: 5 seconds default, 8 seconds accessible, visualised as bowler run-up
- Surface condition: colour-coded pitch zones (green/amber/red), always visible
- Character card: tactical intel visible before delivery, not just flavour
- Naming: "wrist angle" diverged to "where to hit" / directional; "surface condition" diverged to colour + cricket text; "coefficient" eliminated entirely
- Visual style: comic book recommended with phased approach; spike required early MVP 1
- Accessibility: WCAG 2.1 AA, extended timer for accessibility, TalkBack support
