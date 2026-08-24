# Coder Briefing — INCR-002

## Your role
Implement the Delivery aggregate and probability model. Never modify tests.

## Increment file
`workflow/requirements/mvp-1/increments/INCR-002.md`

## What you are implementing
The core gameplay loop: a ball is bowled, the player selects a shot type and wrist angle, and the outcome is resolved by the probability model.

## Key documents to read
1. `workflow/requirements/mvp-1/increments/INCR-002.md` — acceptance criteria
2. `workflow/techsme/ddd.md` — ubiquitous language (use these terms in code)
3. `workflow/techsme/senior-engineer.md` — architecture constraints

## Architecture constraints (from senior-engineer.md)
- **Domain layer is pure Kotlin** — no Android dependencies (ADR-002)
- **Probability model is a pure function** in `domain.delivery` (ADR-005)
- **Deterministic given a seed** — randomness injected via seed, not Math.random()
- **Outcome includes trajectoryHint** (optional) for future rendering (ADR-001)
- **Domain events as Kotlin Flows** (ADR-004)

## Existing code from INCR-001
The following already exists and your implementation should integrate with:
- `domain.match.Match` — aggregate root
- `domain.match.DomainEvent` — sealed class (add new events here)
- `domain.player.BowlerStats` — value object
- `domain.player.BatsmanStats` — value object
- `domain.pitch.SurfaceCondition` — value object

## Implementation to build

### 1. Enums (in `domain.delivery` package)
```kotlin
enum class ShotType {
    Drive, Pull, Cut, Sweep, Defensive, Leave, LegGlance, Slog, ReverseSweep, UpperCut
}

enum class DismissalType {
    Bowled, Caught, LBW, Stumped
    // RunOut excluded per ADR-007 (single batsman model)
}

enum class OutcomeType {
    Runs, Wicket, Wide, NoBall, DotBall
}

enum class Line {
    OffStump, Middle, Leg, OutsideOff, OutsideLeg
}

enum class Length {
    Full, GoodLength, Short, Yorker
}
```

### 2. Value objects (in `domain.delivery` package)
```kotlin
data class BallCharacteristics(
    val line: Line,
    val length: Length,
    val pace: Float,  // 0-1
    val spin: Float   // 0-1
) {
    init {
        require(pace in 0.0f..1.0f) { "pace must be in [0,1]" }
        require(spin in 0.0f..1.0f) { "spin must be in [0,1]" }
    }
}

data class ShotSelection(
    val shotType: ShotType,
    val wristAngle: Float  // degrees of rotation
)

data class Outcome(
    val type: OutcomeType,
    val runs: Int,
    val dismissalType: DismissalType? = null,
    val trajectoryHint: String? = null  // optional, for future rendering
) {
    init {
        require(runs >= 0) { "runs must be >= 0" }
        if (type == OutcomeType.Wicket) {
            requireNotNull(dismissalType) { "dismissalType required when type is Wicket" }
        }
    }
}
```

### 3. Delivery aggregate (in `domain.delivery` package)
```kotlin
class Delivery private constructor(
    val deliveryId: String,
    val deliveryNumber: Int,
    val ballCharacteristics: BallCharacteristics,
    val shotSelection: ShotSelection,
    val outcome: Outcome,
    val isLegal: Boolean
) {
    private val _events = mutableListOf<DomainEvent>()

    fun collectEvents(): List<DomainEvent> = _events.toList()

    companion object {
        fun create(
            deliveryId: String,
            deliveryNumber: Int,
            ballCharacteristics: BallCharacteristics,
            shotSelection: ShotSelection,
            bowlerId: String,
            bowlerStats: BowlerStats,
            batsmanStats: BatsmanStats,
            surfaceCondition: SurfaceCondition,
            seed: Long
        ): Delivery {
            // Resolve outcome using probability model
            val outcome = ProbabilityModel.resolve(
                ballCharacteristics, shotSelection, bowlerStats, batsmanStats, surfaceCondition, seed
            )

            val delivery = Delivery(
                deliveryId = deliveryId,
                deliveryNumber = deliveryNumber,
                ballCharacteristics = ballCharacteristics,
                shotSelection = shotSelection,
                outcome = outcome,
                isLegal = outcome.type != OutcomeType.Wide && outcome.type != OutcomeType.NoBall
            )

            // Emit domain events
            delivery._events.add(DomainEvent.DeliveryBowled(deliveryId, ballCharacteristics, bowlerId))
            delivery._events.add(DomainEvent.ShotPlayed(shotSelection))
            delivery._events.add(DomainEvent.OutcomeResolved(outcome, outcome.runs, outcome.type == OutcomeType.Wicket, outcome.dismissalType))

            return delivery
        }
    }
}
```

### 4. Probability model (in `domain.delivery` package)
This is a pure function — no side effects, deterministic given a seed.

```kotlin
object ProbabilityModel {
    fun resolve(
        ballCharacteristics: BallCharacteristics,
        shotSelection: ShotSelection,
        bowlerStats: BowlerStats,
        batsmanStats: BatsmanStats,
        surfaceCondition: SurfaceCondition,
        seed: Long
    ): Outcome {
        // Use seed to create deterministic random
        val random = java.util.Random(seed)

        // Calculate shot suitability against ball characteristics
        val shotSuitability = calculateShotSuitability(shotSelection.shotType, ballCharacteristics)

        // Apply surface condition modifier
        val surfaceModifier = calculateSurfaceModifier(surfaceCondition)

        // Calculate bowler vs batsman skill differential
        val skillDifferential = calculateSkillDifferential(bowlerStats, batsmanStats)

        // Generate probability distribution
        val distribution = generateDistribution(shotSuitability, surfaceModifier, skillDifferential, random)

        // Sample from distribution to get outcome
        return sampleOutcome(distribution, random)
    }

    // Helper functions for probability calculation
    // Start simple (weighted lookup), iterate to more sophistication
}
```

### 5. Domain events (add to existing DomainEvent.kt)
```kotlin
data class DeliveryBowled(
    val deliveryId: String,
    val ballCharacteristics: BallCharacteristics,
    val bowlerId: String,
    override val timestamp: Long = System.currentTimeMillis()
) : DomainEvent()

data class ShotPlayed(
    val shotSelection: ShotSelection,
    override val timestamp: Long = System.currentTimeMillis()
) : DomainEvent()

data class OutcomeResolved(
    val outcome: Outcome,
    val runsScored: Int,
    val isWicket: Boolean,
    val dismissalType: DismissalType?,
    override val timestamp: Long = System.currentTimeMillis()
) : DomainEvent()
```

### 6. PVT assertions (in `domain.delivery` package)
```kotlin
class DeliveryPvt {
    fun assertDeliveryCanBeCreated() {
        // Verify Delivery class can be instantiated without error
        val ballCharacteristics = BallCharacteristics(Line.OffStump, Length.GoodLength, 0.5f, 0.3f)
        val shotSelection = ShotSelection(ShotType.Drive, 45.0f)
        // If this throws, the service should not start
    }

    fun assertOutcomeEnforcesInvariants() {
        // Verify Outcome rejects Wicket without dismissalType
        try {
            Outcome(OutcomeType.Wicket, 0, null)
            throw AssertionError("Should have rejected Wicket without dismissalType")
        } catch (e: IllegalArgumentException) {
            // Expected — invariant enforced
        }
    }
}
```

## Security requirements (from Cyber SEC-003)
- BallCharacteristics rejects pace/spin outside 0-1
- ShotSelection rejects invalid shotType
- Outcome requires dismissalType when type is Wicket
- All validation at construction time

## Logging requirements (from Ops)
- DeliveryBowled, ShotPlayed, OutcomeResolved events logged with structured format
- OutcomeCalculation logged at DEBUG level with inputs and probability distribution

## What NOT to implement
- UI (no UI in this increment)
- Rendering (separate concern)
- Match orchestration (INCR-003/004)
- RunOut dismissals (excluded per ADR-007)

## When you are done
1. All Feature Owner tests pass
2. PVT assertions work
3. Update the increment file with:
```markdown
## Coder sign-off — [date]
Implementation complete. All Feature Owner tests passing.
Files changed: [list]
PVT assertions added: [what they check]
Notable decisions: [non-obvious choices and why]
Ready for: deployment → Platform Engineer sign-off → DL validation
```

4. Deploy through the pipeline
5. Produce release note at `workflow/requirements/mvp-1/releases/INCR-002-<sha>.md`
6. Notify Platform Engineer for sign-off
