# Feature Owner Briefing — INCR-002

## Your role
Write tests ONLY. Never write implementation code.

## Increment file
`workflow/requirements/mvp-1/increments/INCR-002.md`

## What you are testing
The core gameplay loop: a ball is bowled, the player selects a shot type and wrist angle, and the outcome is resolved by the probability model.

## Key documents to read
1. `workflow/requirements/mvp-1/increments/INCR-002.md` — acceptance criteria
2. `workflow/techsme/ddd.md` — ubiquitous language (use these terms in test names)
3. `workflow/techsme/senior-engineer.md` — architecture constraints

## Existing code from INCR-001
The following already exists and your tests can use:
- `domain.match.Match` — aggregate root
- `domain.match.TossResult` — value object
- `domain.match.DomainEvent` — sealed class with MatchStarted, TossCompleted
- `domain.match.InningsProgress` — value object
- `domain.player.Bowler` — entity with BowlerType, ExperienceClass, BowlerStats
- `domain.player.Batsman` — entity with BatsmanStats
- `domain.player.BowlerStats` — value object (bowlingSkill, accuracy, variation, wideRate, noBallRate)
- `domain.player.BatsmanStats` — value object (battingSkill, timing, power, composure)
- `domain.field.FieldPlacement` — aggregate with FielderPosition
- `domain.pitch.SurfaceCondition` — value object (zoneId, degradation, moisture, roughness)
- `domain.pitch.Weather` — value object with WeatherCondition

## Tests to write (in this order)

### 1. Value object creation and validation
- BallCharacteristics: valid creation with line, length, pace (0-1), spin (0-1)
- BallCharacteristics: rejects pace/spin outside 0-1 (SEC-003)
- BallCharacteristics: rejects invalid line/length enum values
- ShotSelection: valid creation with shotType, wristAngle
- ShotSelection: rejects invalid shotType
- Outcome: valid creation for each outcome type
- Outcome: rejects Wicket without dismissalType
- Outcome: rejects negative runs

### 2. ShotType enum completeness
- All 10 shot types exist: Drive, Pull, Cut, Sweep, Defensive, Leave, LegGlance, Slog, ReverseSweep, UpperCut

### 3. DismissalType enum
- Bowled, Caught, LBW, Stumped exist (RunOut excluded per ADR-007)

### 4. Delivery aggregate creation
- Delivery created with valid BallCharacteristics and ShotSelection
- Delivery emits DeliveryBowled event
- Delivery emits ShotPlayed event

### 5. Outcome resolution (probability model)
- Outcome.resolve() takes (BallCharacteristics, ShotSelection, BowlerStats, BatsmanStats, SurfaceCondition, seed) and returns Outcome
- Same inputs + same seed = same outcome (deterministic, ADR-005)
- Different seed = potentially different outcome
- Wicket outcomes always include dismissalType
- Wide and NoBall produce correct runs (1 run each)
- Runs outcomes are 0-6

### 6. Domain events
- DeliveryBowled event has deliveryId, ballCharacteristics, bowlerId
- ShotPlayed event has shotSelection
- OutcomeResolved event has outcome, runsScored, isWicket, dismissalType

### 7. PVT assertions (verify they exist and work)
- Delivery can be instantiated without error
- Outcome enforces invariants (wicket requires dismissal type)

## Test naming convention
Use ubiquitous language from ddd.md:
- `should_create_delivery_with_valid_ball_characteristics_and_shot_selection`
- `should_reject_ball_characteristics_with_pace_out_of_range`
- `should_resolve_outcome_deterministically_given_same_seed`
- `should_include_dismissal_type_when_outcome_is_wicket`

## What NOT to test
- UI behavior (no UI in this increment)
- Rendering (separate concern)
- Match orchestration (INCR-003/004)

## When you are done
Update the increment file with:
```markdown
## Feature Owner sign-off — [date]
All acceptance criteria covered by tests.
Happy paths: [N] / Unhappy paths: [N] / Edge cases: [N]
All tests passing: yes
Test file(s): [paths]
```

Then notify the Delivery Lead.
