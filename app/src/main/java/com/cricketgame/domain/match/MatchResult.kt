package com.cricketgame.domain.match

/**
 * The outcome of a completed match.
 *
 * Ubiquitous language: MatchResult — Win when target exceeded, Loss when innings
 * ends short of target, Draw when scores are equal after all overs.
 */
enum class MatchResult {
    WIN,
    LOSS,
    DRAW
}
