# Domain Model — Cricket Game
**Status:** initial-complete
**Version:** v1
**Last updated:** 2026-08-20
**Mode:** initial_model

---

## Ubiquitous Language
> These terms are used everywhere: code, tests, tickets, documents.

| Term | Definition | Replaces / do not use |
|---|---|---|
| **Match** | A single T20 game: 20 overs maximum, 10 wickets, one innings where the player bats. | "Game" (too vague), "round" |
| **Innings** | The player's batting period within a match. In this game, one match = one innings. | — |
| **Over** | A set of 6 legal deliveries bowled by the same bowler. | — |
| **Delivery** | A single ball bowled from the bowler to the batsman. The atomic unit of gameplay. | "Ball" (ambiguous — could mean the physical ball object or the event) |
| **Bowler** | An AI-controlled fictional character who delivers the ball. Has type, experience, personality. | — |
| **Batsman** | The player character receiving deliveries. In this game, always the human player. | "Batter" (modern term, but "batsman" is used throughout the opportunity doc) |
| **Bowler Type** | The category of bowling: Fast, Medium-Fast, Off-Spin, Leg-Spin. Determines delivery characteristics. | — |
| **Experience Class** | A bowler's skill tier (e.g., Rookie, Established, Elite). Higher class = better accuracy, fewer wides/no-balls. | — |
| **Nickname** | A bowler's character name (e.g., "The Magician"). Used in UI, cards, and commentary. | — |
| **Quirk** | A non-cricket personality trait of a bowler. Adds entertainment value, may hint at bowling tendencies. | — |
| **Character Card** | A UI panel showing all details of a player (stats, nickname, quirk). Accessible at any time. | "Player card" (ambiguous — could be confused with a collectible card) |
| **Field Placement** | The arrangement of fielders on the ground. Presented to the player before each delivery. Selected from predefined sets based on bowler type and match state. | "Field set" (too technical), "field arrangement" |
| **Fielder** | A non-bowling member of the bowling team positioned on the ground. Their position affects outcome (e.g., caught). | — |
| **Shot** | The batsman's action of hitting the ball. The primary player input. | — |
| **Shot Type** | A category of shot (e.g., Drive, Pull, Cut). The player selects a type, then dials wrist angle. | "Shot selection" (ambiguous — could mean the type or the whole action) |
| **Wrist Angle** | The angle of the bat at point of contact, set by the player via a visual dial. Affects where the ball goes. | — |
| **Line** | The horizontal trajectory of the delivery relative to the stumps. | — |
| **Length** | Where the delivery pitches on the surface relative to the batsman (Full, Good Length, Short, Yorker). | — |
| **Pace** | The speed of the delivery. | — |
| **Spin** | Rotational movement of the ball. Deliberately limited information shown to the player. | — |
| **Ball Characteristics** | A value object bundling line, length, pace, and spin information for a delivery. | — |
| **Surface Condition** | The state of a zone on the pitch. Affects how the ball bounces. Degraded by deliveries, weather, and time. | "Coefficient" (jargon — Customer flagged this), "surface coefficient" |
| **Pitch** | The prepared strip of ground where the ball bounces. Divided into zones, each with its own surface condition. | "Ground" (that's the venue), "surface" (too vague) |
| **Ground** | The cricket venue where the match is played. Has a name, weather, and associated pitch. | "Venue" (acceptable synonym but prefer "ground") |
| **Weather** | Match-level atmospheric condition (e.g., sunny, overcast, humid). Affects pitch degradation. | — |
| **Target** | The score the player must reach to win. Set by the player if toss is won; provided as a number if toss is lost. | — |
| **Toss** | The coin toss at match start. Determines whether the player sets a target or chases one. | — |
| **Runs** | The unit of scoring. Accumulated through deliveries. | — |
| **Wicket** | The batsman being dismissed (out). The player has 10 wickets in a match. | "Out" (acceptable in context but "wicket" is the domain term) |
| **Boundary** | The edge of the playing area. Ball reaching it along the ground = 4 runs; in the air = 6 runs. | — |
| **Wide** | An illegal delivery too far from the batsman. Awards 1 run, ball is re-bowled. | — |
| **No Ball** | An illegal delivery (e.g., bowler oversteps). Awards 1 run, ball is re-bowled. | — |
| **Dismissal** | How the batsman gets out: Bowled, Caught, LBW, Run Out, Stumped. | "Wicket" (ambiguous — could mean the stumps or the dismissal) |
| **Outcome** | The result of a delivery: runs scored, dismissal type, wide, no ball. | — |
| **Innings Progress** | The state of the match at any point: overs completed, wickets fallen, current score. | "Match state" (too vague) |

**Conflicts with Researcher glossary:**

| Researcher term | DDD term | Resolution |
|---|---|---|
| "Ball" (as delivery) | "Delivery" | DDD uses "Delivery" for the event/object. "Ball" refers to the physical object. Avoids ambiguity in code. |
| "Coefficient of the surface" | "Surface Condition" | DDD uses player-facing term. "Coefficient" is implementation detail. Customer flagged "coefficients shift" as jargon. |
| "Field set" | "Field Placement" | DDD uses more natural term. "Field set" sounds like a technical configuration. |
| "Card" | "Character Card" | DDD adds "Character" for clarity. "Card" alone is too generic. |
| "Ground" vs "Pitch" | Both used, distinct meanings | Resolved: "Ground" = venue; "Pitch" = the strip where the ball bounces. Researcher glossary flagged this ambiguity; DDD resolves it. |

---

## Bounded Contexts

### Match
**Description:** The match lifecycle — toss, innings, overs, deliveries, score, result. This is the gameplay context that orchestrates everything.
**Key language:** Match, Innings, Over, Delivery, Toss, Target, Runs, Wicket, Outcome, Innings Progress

### Delivery
**Description:** The mechanics of a single ball — what the bowler delivers, what the batsman does, and what happens. The core gameplay loop lives here.
**Key language:** Delivery, Ball Characteristics, Shot, Shot Type, Wrist Angle, Outcome, Dismissal

### Player
**Description:** The bowler and batsman models — their types, skills, personality, and character cards. This is the character system.
**Key language:** Bowler, Batsman, Bowler Type, Experience Class, Nickname, Quirk, Character Card

### Field
**Description:** The fielding arrangement and how it relates to the bowler's plan. Presented to the player as tactical intel.
**Key language:** Field Placement, Fielder, Slips

### Pitch
**Description:** The physical playing surface and its conditions. Models degradation, weather effects, and zone-by-zone variation.
**Key language:** Pitch, Surface Condition, Weather, Ground

### Team (deferred — MVP 3)
**Description:** Career progression, team management, difficulty scaling across matches. Thin in MVP 1/2.
**Key language:** Team (future), Progression (future)

### Context Map

| Context A | Relationship | Context B | Notes |
|---|---|---|---|
| Match | **Orchestrates** | Delivery | Match creates and sequences deliveries. Delivery reports outcome back to Match. |
| Delivery | **Uses** | Player | Delivery references the active Bowler and Batsman. |
| Delivery | **Uses** | Field | Delivery references the current Field Placement for outcome calculation. |
| Delivery | **Uses** | Pitch | Delivery references the current Surface Condition at the ball's zone. |
| Player | **Referenced by** | Match | Match holds the bowling roster and batting order. |
| Pitch | **Referenced by** | Match | Match holds the Ground and Pitch for the match. |
| Field | **Referenced by** | Player | Field Placement is selected based on Bowler Type. |

---

## Aggregates

### Match (root: Match)
**Invariants:**
- Maximum 20 overs (120 legal deliveries)
- Maximum 10 wickets
- Each bowler bowls maximum 4 overs (OQ-25: standard T20 rules — assumption, human confirms)
- Match ends when: all overs bowled, all wickets fallen, or target reached

**Owns:**
- Over (ordered collection)
- Innings Progress (value object, updated per delivery)

**References by ID:**
- Ground
- Bowler roster (list of Bowler IDs)
- Active Bowler ID
- Active Batsman ID (single batsman — OQ-21 assumption, human confirms)

---

### Bowler (root: Bowler)
**Invariants:**
- Bowler Type is immutable for the match
- Experience Class is immutable for the match
- Overs bowled cannot exceed 4

**Owns:**
- Bowler Stats (value object)
- Character Card data (nickname, quirk)

**References by ID:**
- None (self-contained)

---

### Batsman (root: Batsman)
**Invariants:**
- Skills are immutable for the match
- Only one active Batsman at a time (single batsman model — MVP 1 assumption)

**Owns:**
- Batsman Stats (value object)
- Character Card data (nickname, quirk — deferred to MVP 2)

**References by ID:**
- None (self-contained)

---

### Pitch (root: Pitch)
**Invariants:**
- Surface Condition degrades monotonically during the match (does not improve)
- Each zone degrades independently based on deliveries to that zone
- Weather is a match-level constant (does not change mid-match)

**Owns:**
- Surface Condition grid (value objects per zone)
- Weather (value object)

**References by ID:**
- Ground (the venue this pitch belongs to)

---

### Field Placement (root: Field Placement)
**Invariants:**
- Exactly 11 fielding positions per placement (including bowler and wicket-keeper)
- Field Placement changes between overs (bowler's tactical choice), not within an over

**Owns:**
- Fielder positions (value objects)

**References by ID:**
- Bowler Type (placement is selected based on bowler type)

---

## Entities

| Entity | Identity | Key attributes | Aggregate |
|---|---|---|---|
| Match | Match ID | format (T20), maxOvers (20), maxWickets (10), target, result | Match |
| Over | Over ID (within match) | overNumber, bowlerId, deliveries (ordered), isComplete | Match |
| Delivery | Delivery ID (within over) | deliveryNumber, ballCharacteristics, shotSelection, outcome, isLegal | Match |
| Bowler | Bowler ID | bowlerType, experienceClass, nickname, quirk, oversBowled | Bowler |
| Batsman | Batsman ID | battingSkill, nickname, quirk | Batsman |
| Ground | Ground ID | name, location | — (referenced by Match and Pitch) |

---

## Value Objects

| Value Object | Attributes | Validated by |
|---|---|---|
| **BallCharacteristics** | line (enum: Off Stump, Middle, Leg, Outside Off, Outside Leg), length (enum: Full, Good Length, Short, Yorker), pace (float 0–1), spin (float 0–1, deliberately imprecise) | Range checks. Line and length must be valid enum values. Pace and spin in [0, 1]. |
| **ShotSelection** | shotType (enum), wristAngle (float, degrees of rotation) | shotType must be a valid enum. wristAngle in valid range. |
| **Outcome** | type (enum: Runs, Wicket, Wide, No Ball, Dot Ball), runs (int), dismissalType (nullable enum: Bowled, Caught, LBW, Stumped, Run Out) | If type=Wicket, dismissalType must be set. Runs >= 0. |
| **SurfaceCondition** | zoneId (string), degradation (float 0–1), moisture (float 0–1), roughness (float 0–1) | All values in [0, 1]. Degradation is monotonically non-decreasing. |
| **Weather** | condition (enum: Sunny, Overcast, Humid, Cloudy), temperature (float), humidity (float 0–1) | condition must be valid enum. Humidity in [0, 1]. |
| **FieldPlacement** | fielders (list of FielderPosition) | Exactly 11 positions. No duplicates. |
| **FielderPosition** | positionName (string: e.g., "Slips", "Cover", "Mid-On"), x (float), y (float) | Coordinates within ground bounds. |
| **BowlerStats** | bowlingSkill (float 0–1), accuracy (float 0–1), variation (float 0–1), wideRate (float 0–1), noBallRate (float 0–1) | All in [0, 1]. Derived from bowlerType + experienceClass. |
| **BatsmanStats** | battingSkill (float 0–1), timing (float 0–1), power (float 0–1), composure (float 0–1) | All in [0, 1]. |
| **InningsProgress** | oversCompleted (int), ballsThisOver (int 0–5), wicketsFallen (int 0–10), currentScore (int), target (nullable int) | Wickets <= 10. Balls <= 6 per over. Score >= 0. |
| **TossResult** | winner (enum: Player, AI), decision (enum: Bat, Field) | — |
| **CharacterInfo** | nickname (string), quirk (string) | Non-empty strings. |

---

## Domain Events

| Event | Raised by | Consumed by | Payload |
|---|---|---|---|
| **MatchStarted** | Match | UI, Analytics | matchId, groundId, weather, tossResult |
| **TossCompleted** | Match | UI | tossResult, battingFirst |
| **OverStarted** | Match | UI, Pitch | overNumber, bowlerId |
| **DeliveryBowled** | Delivery | Match, UI, Pitch | deliveryId, ballCharacteristics, bowlerId |
| **ShotPlayed** | Delivery | UI | shotSelection |
| **OutcomeResolved** | Delivery | Match, UI, Analytics | outcome, runsScored, isWicket, dismissalType |
| **WicketFallen** | Match | UI, Analytics | wicketNumber, batsmanId, dismissalType, deliveryId |
| **BoundaryScored** | Match | UI, Analytics | runs (4 or 6), batsmanId, deliveryId |
| **OverCompleted** | Match | UI, Pitch | overNumber, runsThisOver, wicketsThisOver |
| **BowlerChanged** | Match | UI, Field | newBowlerId, bowlerType |
| **FieldPlacementChanged** | Field | UI | newFieldPlacement |
| **SurfaceConditionChanged** | Pitch | Delivery (for next delivery) | zoneId, newCondition |
| **TargetReached** | Match | UI, Analytics | finalScore, target, wicketsRemaining |
| **InningsCompleted** | Match | UI, Analytics | finalScore, wicketsFallen, oversCompleted, result |
| **MatchCompleted** | Match | UI, Analytics | matchId, result, finalScore |

---

## Draft Schemas

### Match
```json
{
  "matchId": "string — unique identifier",
  "groundId": "string — reference to Ground",
  "format": "T20",
  "maxOvers": "int — 20",
  "maxWickets": "int — 10",
  "tossResult": { "winner": "Player|AI", "decision": "Bat|Field" },
  "target": "int|null — set if chasing, null if setting",
  "overs": "Over[] — ordered list",
  "inningsProgress": "InningsProgress — current state",
  "bowlerRoster": "string[] — Bowler IDs",
  "activeBowlerId": "string — current Bowler ID",
  "activeBatsmanId": "string — current Batsman ID",
  "result": "Win|Loss|null — set at match end"
}
```

### Over
```json
{
  "overId": "string — unique within match",
  "overNumber": "int — 0-indexed",
  "bowlerId": "string — reference to Bowler",
  "deliveries": "Delivery[] — ordered list, max 6 legal",
  "isComplete": "boolean"
}
```

### Delivery
```json
{
  "deliveryId": "string — unique within over",
  "deliveryNumber": "int — 0-indexed within over",
  "ballCharacteristics": "BallCharacteristics",
  "shotSelection": "ShotSelection|null — null if wide/no-ball before shot",
  "outcome": "Outcome",
  "isLegal": "boolean — false for wide/no-ball"
}
```

### Bowler
```json
{
  "bowlerId": "string — unique identifier",
  "bowlerType": "Fast|MediumFast|OffSpin|LegSpin",
  "experienceClass": "Rookie|Established|Elite",
  "nickname": "string — e.g. 'The Magician'",
  "quirk": "string — non-cricket personality trait",
  "stats": "BowlerStats"
}
```

### Batsman
```json
{
  "batsmanId": "string — unique identifier",
  "nickname": "string — deferred to MVP 2",
  "quirk": "string — deferred to MVP 2",
  "stats": "BatsmanStats"
}
```

### Pitch
```json
{
  "pitchId": "string — unique identifier",
  "groundId": "string — reference to Ground",
  "weather": "Weather",
  "zones": "SurfaceCondition[] — grid of zones, each with degradation/moisture/roughness",
  "ballAge": "int — number of deliveries bowled (affects swing/degradation)"
}
```

### Ground
```json
{
  "groundId": "string — unique identifier",
  "name": "string — e.g. 'Lord's', 'MCG'",
  "location": "string — city/country",
  "defaultWeather": "Weather — typical conditions for this ground"
}
```

### FieldPlacement
```json
{
  "placementId": "string — unique identifier",
  "name": "string — e.g. 'Attacking', 'Defensive'",
  "bowlerType": "BowlerType — which bowler type this suits",
  "fielders": "FielderPosition[] — exactly 11"
}
```

### ShotType (enum)
```
Drive, Pull, Cut, Sweep, Defensive, Leave, LegGlance, Slog, ReverseSweep, UpperCut
```
**MVP 1 assumption:** 10 shot types covering vertical-bat (Drive, Defensive, Leave, LegGlance), horizontal-bat (Pull, Cut, Sweep), and unorthodox (Slog, ReverseSweep, UpperCut). Human to confirm — OQ-17.

### BowlerType (enum)
```
Fast, MediumFast, OffSpin, LegSpin
```
**MVP 1 assumption:** 4 types as specified in roadmap ("at least 2 bowler types"). Human to confirm — OQ-18.

### ExperienceClass (enum)
```
Rookie, Established, Elite
```

### DismissalType (enum)
```
Bowled, Caught, LBW, Stumped, RunOut
```

---

## Assumptions requiring human confirmation

| ID | Assumption | Rationale | Open Question | Human Decision (2026-08-20) |
|---|---|---|---|---|
| A-1 | Single batsman (no partnership, no non-striker, no run-outs) | Simplifies MVP 1. Customer flagged this as a missing scenario — revisit if human wants partnerships. | OQ-21 | **Confirmed for MVP 1.** Partnerships (YES/NO/WAIT calling, run judging, fielder return mechanics) planned for later increment. Too complex for MVP 1. |
| A-2 | 10 shot types: Drive, Pull, Cut, Sweep, Defensive, Leave, LegGlance, Slog, ReverseSweep, UpperCut | Curated from research. Covers key categories. UX to validate UI feasibility under time pressure. | OQ-17 | **Confirmed with variations.** Include front/backfoot defence as separate, late/square cut as separate. Helps categorisation. |
| A-3 | 4 bowler types: Fast, Medium-Fast, Off-Spin, Leg-Spin | Standard cricket bowler types. Roadmap says "at least 2" for MVP 1. | OQ-18 | **Confirmed.** |
| A-4 | Standard T20 bowling restriction: max 4 overs per bowler | Standard rules. Human may want to simplify. | OQ-25 | **Confirmed — apply standard T20 rules.** |
| A-5 | AI target is a pre-calculated number, not a simulated innings | Simplest model. Keeps MVP 1 scope tight. | OQ-20 | **Confirmed for MVP 1.** Human wants fielding side gameplay in a future increment (not MVP 1). |
| A-6 | Team/Career progression is deferred entirely to MVP 3 | Roadmap confirms this. Domain model has a placeholder bounded context. | OQ-19 | **Modified.** MVP 1 should record team's number of games and experience. Deeper mechanics in MVP 2/3. |

---

## Increment review notes

| Date | Increment | Change | Impact on model |
|---|---|---|---|
| — | — | — | — |

---

## PO Approval
**Status:** approved
**Date:** 2026-08-20
**Notes:** Domain model is fully compatible with the product vision. The ubiquitous language correctly translates the character-driven nature of the product — Nickname, Quirk, Character Card, and Experience Class are first-class domain concepts, not afterthoughts. The replacement of "coefficient" with "Surface Condition" aligns with the Customer's feedback and the vision's emphasis on readable, non-jargon presentation. The bounded context map correctly separates Match, Delivery, Player, Field, and Pitch — each maps to a distinct feature area in the roadmap. The single batsman model (A-1) and Team deferral to MVP 3 (A-6) are confirmed roadmap decisions, not gaps. The 10 shot types and 4 bowler types provide sufficient variety for MVP 1's core loop validation.
