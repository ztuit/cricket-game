package com.cricketgame.domain.match

import com.cricketgame.domain.player.BowlerType

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
 * Orchestrates the toss and creates InningsProgress.
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
