# INCR-007 — Bowler character system
**MVP:** 1
**Status:** open
**Priority:** should
**Complexity:** medium
**Dependencies:** INCR-001
**Created:** 2026-08-20

---
## Purpose
Implement bowler personalities: nicknames, quirks, experience classes, and character cards. The bowler is not just stats — they are a character the player engages with. This is the differentiator that makes the game character-driven.

**Ubiquitous language terms involved:**
Bowler, Nickname, Quirk, Experience Class, Character Card, BowlerType, BowlerStats

---
## Acceptance criteria
- [ ] Each bowler has a nickname (e.g., "The Magician", "Golden Arm")
- [ ] Each bowler has a quirk (non-cricket personality trait)
- [ ] Experience class shown as star rating or tier label on character card: Rookie, Established, Elite
- [ ] Character card accessible via tap on bowler name — but NOT during delivery countdown (human decision)
- [ ] Character card shows: Nickname, Bowler Type, Experience Class, Quirk, tactical hints derived from stats
- [ ] Nickname shown prominently during delivery loop as primary bowler identifier
- [ ] Bowler type shown as secondary info (icon or label)
- [ ] At least 5 bowlers with distinct nicknames and quirks

---
## Technical notes
**SE:** Bowler entity owns CharacterInfo value object (nickname, quirk). Character card is a UI overlay (bottom sheet). The "not during delivery countdown" constraint requires a state check: if the delivery timer is active, the tap is ignored or the card is not openable. Tactical hints are derived from BowlerStats, not hardcoded — e.g., "Tends to bowl wide outside off" if accuracy is low.

**Cyber:** None — fictional character data, no PII.

**UX:** Nickname is the primary identifier (UX-009). Character card accessible via tap on nickname (UX-008) but NOT during delivery countdown (human override of UX Expert's original proposal). Card shows type, experience, quirk, tactical hint. Bottom sheet overlay pattern.

**Ops:** None — character data is static.

**DDD:** Bowler entity with CharacterInfo value object. Nickname and Quirk are string attributes. ExperienceClass is an enum. BowlerStats derives tactical hints.

---
## Deployment validation
1. Run on physical device: bowler nickname displayed prominently during delivery loop
2. Verify tapping nickname opens character card (bottom sheet)
3. Verify character card shows: nickname, type, experience class, quirk, tactical hint
4. Verify character card is NOT openable during delivery countdown
5. Verify at least 5 distinct bowlers with unique nicknames and quirks
6. Verify tactical hints change based on bowler stats
