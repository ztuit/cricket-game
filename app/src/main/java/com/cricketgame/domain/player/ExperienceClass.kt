package com.cricketgame.domain.player

/**
 * A bowler's skill tier. Higher class = better accuracy, fewer wides/no-balls.
 *
 * Ubiquitous language: ExperienceClass — not "skill level" or "tier".
 */
enum class ExperienceClass {
    ROOKIE,
    ESTABLISHED,
    ELITE
}
