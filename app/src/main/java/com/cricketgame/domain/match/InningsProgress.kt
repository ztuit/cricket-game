package com.cricketgame.domain.match

import com.cricketgame.domain.delivery.Outcome
import com.cricketgame.domain.delivery.OutcomeType

/**
 * The state of the innings at any point: overs completed, wickets fallen, current score.
 *
 * Ubiquitous language: InningsProgress — not "match state" (too vague).
 *
 * Invariants (SEC-003): wicketsFallen <= 10, ballsThisOver <= 6, currentScore >= 0.
 *
 * Immutable — updated via copy() returning a new instance.
 */
data class InningsProgress(
    val oversCompleted: Int,
    val ballsThisOver: Int,
    val wicketsFallen: Int,
    val currentScore: Int,
    val target: Int?
) {
    init {
        require(wicketsFallen in 0..10) { "wicketsFallen must be in [0,10], was $wicketsFallen" }
        require(ballsThisOver in 0..6) { "ballsThisOver must be in [0,6], was $ballsThisOver" }
        require(currentScore >= 0) { "currentScore must be >= 0, was $currentScore" }
    }

    /**
     * Pure function: returns a new InningsProgress with the outcome applied.
     *
     * - Runs and Dot Ball: add runs to score, increment balls if legal
     * - Wicket: increment wickets, increment balls (legal delivery)
     * - Wide and No Ball: add 1 run, do NOT increment balls (illegal delivery)
     * - When ballsThisOver reaches 6: over completes, reset to 0, increment oversCompleted
     */
    fun update(outcome: Outcome): InningsProgress {
        val newScore = currentScore + outcome.runs
        val newWickets = if (outcome.type == OutcomeType.WICKET) wicketsFallen + 1 else wicketsFallen

        // Only legal deliveries increment the ball count
        val newBalls = if (outcome.isLegal) ballsThisOver + 1 else ballsThisOver

        // Check if over completes (6 legal deliveries)
        return if (newBalls >= 6) {
            copy(
                currentScore = newScore,
                wicketsFallen = newWickets,
                ballsThisOver = 0,
                oversCompleted = oversCompleted + 1
            )
        } else {
            copy(
                currentScore = newScore,
                wicketsFallen = newWickets,
                ballsThisOver = newBalls
            )
        }
    }

    /**
     * Whether the innings is complete: all overs bowled, all wickets fallen,
     * or target exceeded (if chasing).
     */
    fun isInningsComplete(maxOvers: Int, maxWickets: Int): Boolean {
        if (oversCompleted >= maxOvers) return true
        if (wicketsFallen >= maxWickets) return true
        val t = target
        if (t != null && currentScore > t) return true
        return false
    }

    companion object {
        /**
         * Factory method for the initial state at the start of an innings.
         */
        fun initial(target: Int?): InningsProgress = InningsProgress(
            oversCompleted = 0,
            ballsThisOver = 0,
            wicketsFallen = 0,
            currentScore = 0,
            target = target
        )
    }
}
