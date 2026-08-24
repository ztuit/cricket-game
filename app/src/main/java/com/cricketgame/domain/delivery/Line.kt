package com.cricketgame.domain.delivery

/**
 * Horizontal trajectory of the delivery relative to the stumps.
 *
 * Ubiquitous language: Line — not "direction" or "trajectory".
 */
enum class Line {
    OFF_STUMP,
    MIDDLE,
    LEG,
    OUTSIDE_OFF,
    OUTSIDE_LEG
}
