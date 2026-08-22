package com.cricketgame.domain.player

/**
 * Batsman statistics value object. All attributes in [0, 1].
 *
 * Ubiquitous language: BatsmanStats — not "batting attributes" or "batting metrics".
 */
data class BatsmanStats(
    val battingSkill: Float,
    val timing: Float,
    val power: Float,
    val composure: Float
) {
    init {
        require(battingSkill in 0.0f..1.0f) { "battingSkill must be in [0,1], was $battingSkill" }
        require(timing in 0.0f..1.0f) { "timing must be in [0,1], was $timing" }
        require(power in 0.0f..1.0f) { "power must be in [0,1], was $power" }
        require(composure in 0.0f..1.0f) { "composure must be in [0,1], was $composure" }
    }
}
