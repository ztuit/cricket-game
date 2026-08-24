package com.cricketgame.domain.delivery

/**
 * Value object representing the batsman's shot choice.
 *
 * Ubiquitous language: ShotSelection — not "shot choice" or "batting input".
 *
 * shotType: one of the 10 predefined ShotType values.
 * wristAngle: float value — UI input mechanism is a separate concern (INCR-005/006).
 */
data class ShotSelection(
    val shotType: ShotType,
    val wristAngle: Float
)
