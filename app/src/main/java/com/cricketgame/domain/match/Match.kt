package com.cricketgame.domain.match

import com.cricketgame.domain.delivery.Outcome
import com.cricketgame.domain.delivery.OutcomeType

/**
 * Match aggregate root.
 *
 * A single T20 game: 20 overs maximum, 10 wickets, one innings where the player bats.
 *
 * Invariants:
 * - Maximum 20 overs (120 legal deliveries)
 * - Maximum 10 wickets
 * - Each bowler bowls maximum 4 overs (standard T20 rules)
 * - Match ends when: all overs bowled, all wickets fallen, or target reached
 *
 * Orchestrates the toss, tracks InningsProgress, and determines the match result.
 */
class Match private constructor(
    val matchId: String,
    val groundId: String,
    val maxOvers: Int,
    val maxWickets: Int,
    val bowlerRosterIds: List<String>,
    val target: Int?
) {
    private val _events = mutableListOf<DomainEvent>()

    /**
     * Current state of the innings: runs, wickets, overs.
     * Updated immutably after each delivery via processOutcome().
     */
    var inningsProgress: InningsProgress = InningsProgress.initial(target)
        private set

    /**
     * Whether the match has ended. Once true, processOutcome() will reject further deliveries.
     */
    var isComplete: Boolean = false
        private set

    /**
     * The match result, set when the match ends. Null until isComplete is true.
     */
    var result: MatchResult? = null
        private set

    /**
     * Process a delivery outcome: update InningsProgress, check boundaries/wickets/overs/innings,
     * and emit domain events.
     *
     * @throws IllegalStateException if the match is already complete
     */
    fun processOutcome(outcome: Outcome) {
        check(!isComplete) { "Match $matchId is already complete — cannot process further outcomes" }

        val previousProgress = inningsProgress
        val newProgress = previousProgress.update(outcome)
        inningsProgress = newProgress

        // Emit scoring events based on outcome type
        emitScoringEvents(outcome, newProgress)

        // Check if over completed (ballsThisOver went from non-zero to 0)
        if (newProgress.ballsThisOver == 0 && (previousProgress.ballsThisOver > 0 || outcome.isLegal)) {
            // Over completed — the update() reset ballsThisOver to 0 and incremented oversCompleted
            _events.add(DomainEvent.OverCompleted(overNumber = newProgress.oversCompleted - 1))
        }

        // Check innings end conditions
        if (newProgress.isInningsComplete(maxOvers, maxWickets)) {
            completeInnings(newProgress)
        }
    }

    /**
     * Perform the toss with a random winner.
     * The winner chooses Bat or Field.
     */
    fun performToss(): TossResult {
        val winner = if (Math.random() < 0.5) TossResult.Winner.PLAYER else TossResult.Winner.AI
        return performTossInternal(winner)
    }

    /**
     * Test helper: force a specific toss winner.
     * Used by Feature Owner tests to control the outcome.
     */
    fun performTossForTest(winner: TossResult.Winner): TossResult {
        return performTossInternal(winner)
    }

    /**
     * Collect all domain events emitted by this match.
     * Used for testing and event forwarding (ADR-004).
     */
    fun collectEvents(): List<DomainEvent> = _events.toList()

    private fun emitScoringEvents(outcome: Outcome, progress: InningsProgress) {
        // Boundary detection: 4 or 6 runs scored via RUNS outcome
        if (outcome.type == OutcomeType.RUNS && (outcome.runs == 4 || outcome.runs == 6)) {
            _events.add(DomainEvent.BoundaryScored(runs = outcome.runs))
        }

        // Wicket event
        if (outcome.type == OutcomeType.WICKET) {
            _events.add(
                DomainEvent.WicketFallen(
                    wicketNumber = progress.wicketsFallen,
                    dismissalType = outcome.dismissalType!!
                )
            )
        }
    }

    private fun completeInnings(progress: InningsProgress) {
        _events.add(
            DomainEvent.InningsCompleted(
                finalScore = progress.currentScore,
                wicketsFallen = progress.wicketsFallen,
                oversCompleted = progress.oversCompleted
            )
        )

        // Determine match result
        val matchResult = determineResult(progress)
        result = matchResult
        isComplete = true

        // Target reached event (only when chasing and score exceeds target)
        val t = progress.target
        if (t != null && progress.currentScore > t) {
            _events.add(
                DomainEvent.TargetReached(
                    finalScore = progress.currentScore,
                    target = t,
                    wicketsRemaining = maxWickets - progress.wicketsFallen
                )
            )
        }

        _events.add(
            DomainEvent.MatchCompleted(
                matchId = matchId,
                result = matchResult,
                finalScore = progress.currentScore
            )
        )
    }

    /**
     * Determine the match result based on InningsProgress.
     *
     * - Win: score exceeds target (when chasing)
     * - Loss: innings ends short of target (when chasing)
     * - Draw: scores equal after all overs, or batting first (no target)
     */
    private fun determineResult(progress: InningsProgress): MatchResult {
        val t = progress.target ?: return MatchResult.DRAW
        return when {
            progress.currentScore > t -> MatchResult.WIN
            progress.currentScore < t -> MatchResult.LOSS
            else -> MatchResult.DRAW
        }
    }

    private fun performTossInternal(winner: TossResult.Winner): TossResult {
        val decision = if (Math.random() < 0.5) TossResult.Decision.BAT else TossResult.Decision.FIELD
        val tossResult = TossResult(winner = winner, decision = decision)
        _events.add(DomainEvent.TossCompleted(tossResult = tossResult))
        return tossResult
    }

    companion object {
        /**
         * Create a new Match with validated invariants.
         *
         * @param matchId Unique identifier
         * @param groundId Reference to the Ground where this match is played
         * @param maxOvers Maximum overs (0-20 for T20)
         * @param maxWickets Maximum wickets (0-10)
         * @param bowlerRosterIds At least 5 bowler IDs covering all 4 BowlerTypes
         * @param target Null if batting first (setting), positive integer if chasing
         */
        fun create(
            matchId: String,
            groundId: String,
            maxOvers: Int,
            maxWickets: Int,
            bowlerRosterIds: List<String>,
            target: Int? = null
        ): Match {
            require(maxOvers in 0..20) { "maxOvers must be in [0,20], was $maxOvers" }
            require(maxWickets in 0..10) { "maxWickets must be in [0,10], was $maxWickets" }
            require(bowlerRosterIds.size >= 5) { "bowlerRosterIds must have at least 5 entries, had ${bowlerRosterIds.size}" }

            val match = Match(
                matchId = matchId,
                groundId = groundId,
                maxOvers = maxOvers,
                maxWickets = maxWickets,
                bowlerRosterIds = bowlerRosterIds,
                target = target
            )

            match._events.add(DomainEvent.MatchStarted(matchId = matchId, groundId = groundId))

            return match
        }
    }
}
