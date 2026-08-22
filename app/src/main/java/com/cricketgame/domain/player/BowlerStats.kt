package com.cricketgame.domain.player

/**
 * Bowler statistics value object. All attributes in [0, 1].
 *
 * Derived from bowlerType + experienceClass.
 * Ubiquitous language: BowlerStats — not "bowler attributes" or "bowling metrics".
 */
data class BowlerStats(
    val bowlingSkill: Float,
    val accuracy: Float,
    val variation: Float,
    val wideRate: Float,
    val noBallRate: Float
) {
    init {
        require(bowlingSkill in 0.0f..1.0f) { "bowlingSkill must be in [0,1], was $bowlingSkill" }
        require(accuracy in 0.0f..1.0f) { "accuracy must be in [0,1], was $accuracy" }
        require(variation in 0.0f..1.0f) { "variation must be in [0,1], was $variation" }
        require(wideRate in 0.0f..1.0f) { "wideRate must be in [0,1], was $wideRate" }
        require(noBallRate in 0.0f..1.0f) { "noBallRate must be in [0,1], was $noBallRate" }
    }
}
