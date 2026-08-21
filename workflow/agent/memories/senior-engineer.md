# Senior Engineer Memory

## [2026-08-20] Initial architecture produced
**Type:** decision
**Context:** First invocation after DDD Expert completed the domain model. Product is a cricket strategy game for Android (API 31+), offline-first, with a sophisticated surface physics model.
**What happened:** Produced `workflow/techsme/senior-engineer.md` with full architecture. Key decisions: Kotlin + Jetpack Compose, pure Kotlin domain layer (no Android deps), rendering abstraction to support undecided visual style (2D top-down vs comic book), Room for persistence, repository pattern for future server sync.
**Impact:** All subsequent agents must respect the domain/rendering separation. The visual style prototype spike must happen early in MVP 1 — it affects what data the renderer needs from the engine.
**Status:** open — awaiting parallel SME review from Cyber, UX, Ops, PE, Test Engineer.

## [2026-08-20] Key ambiguity: visual style
**Type:** open-question
**Context:** Product vision says "2D top-down or comic book" — needs prototyping. Customer feedback wants comic book style to match the character-driven nature.
**What happened:** Architecture designed with `GameRenderer` interface to decouple rendering from game logic. Both visual styles are technically feasible. Comic book may need sprite sheets (LibGDX), 2D top-down works with Compose Canvas.
**Impact:** UX Expert will drive this decision. SE must support both paths without committing to either. The prototype spike is the decision point.
**Status:** open

## [2026-08-20] Key ambiguity: outcome animation depth
**Type:** open-question
**Context:** Customer asked "what happens when I get out? Is there a replay?" The `Outcome` value object currently holds runs/dismissal type but not trajectory data.
**What happened:** Added `trajectoryHint` concept to ADR notes — the Outcome should carry enough data for a future renderer to animate ball path, but MVP 1 uses simple result display.
**Impact:** If the prototype spike reveals that animation is critical to the "feel" of the game, the Outcome value object may need expanding early.
**Status:** open

## [2026-08-20] Customer wants characters in MVP 1
**Type:** preference
**Context:** Customer vision review pushed back on characters being MVP 2 only. Wants at least bowler nicknames and visible traits in MVP 1 for first impression.
**What happened:** Architecture supports this — the `Player` context has `CharacterInfo` (nickname, quirk) already modelled. Adding it to MVP 1 is a scope decision for TPO/PO, not an architectural change.
**Impact:** If approved, MVP 1 increments can include character card display with zero architectural change.
**Status:** open — TPO/PO decision
