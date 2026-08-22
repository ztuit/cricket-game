package com.cricketgame.domain.match

/**
 * The state of the innings at any point: overs completed, wickets fallen, current score.
 *
 * Ubiquitous language: InningsProgress — not "match state" (too vague).
 *
 * Invariants (SEC-003): wicketsFallen <= 10, ballsThisOver <= 6, currentScore >= 0.
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
