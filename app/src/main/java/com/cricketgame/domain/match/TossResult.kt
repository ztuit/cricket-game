package com.cricketgame.domain.match

/**
 * Result of the toss at match start.
 *
 * Ubiquitous language: Toss — the coin toss that determines whether the player
 * sets a target or chases one. Winner chooses Bat or Field.
 */
data class TossResult(
    val winner: Winner,
    val decision: Decision
) {
    enum class Winner { PLAYER, AI }
    enum class Decision { BAT, FIELD }
}
