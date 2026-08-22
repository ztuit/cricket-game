package com.cricketgame.domain.player

/**
 * The player character receiving deliveries. In this game, always the human player.
 *
 * Single batsman model (A-1, confirmed for MVP 1).
 * Skills are immutable for the match.
 *
 * Ubiquitous language: Batsman — not "batter" (modern term, but opportunity doc uses "batsman").
 */
class Batsman private constructor(
    val batsmanId: String,
    val nickname: String,
    val quirk: String,
    val stats: BatsmanStats
) {
    companion object {
        /**
         * Create a Batsman with default stats.
         */
        fun create(
            batsmanId: String,
            nickname: String,
            quirk: String
        ): Batsman {
            return Batsman(
                batsmanId = batsmanId,
                nickname = nickname,
                quirk = quirk,
                stats = BatsmanStats(
                    battingSkill = 0.7f,
                    timing = 0.65f,
                    power = 0.6f,
                    composure = 0.7f
                )
            )
        }
    }
}
