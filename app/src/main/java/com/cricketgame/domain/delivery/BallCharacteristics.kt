package com.cricketgame.domain.delivery

/**
 * Value object bundling line, length, pace, and spin information for a delivery.
 *
 * Ubiquitous language: BallCharacteristics — not "ball info" or "delivery attributes".
 *
 * Security requirement (SEC-003): pace and spin must be in [0, 1].
 * All validation at construction time.
 */
data class BallCharacteristics(
    val line: Line,
    val length: Length,
    val pace: Float,
    val spin: Float
) {
    init {
        require(pace in 0.0f..1.0f) { "pace must be in [0,1], was $pace" }
        require(spin in 0.0f..1.0f) { "spin must be in [0,1], was $spin" }
    }
}
