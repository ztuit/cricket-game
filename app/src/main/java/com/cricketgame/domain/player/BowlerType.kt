package com.cricketgame.domain.player

/**
 * The category of bowling. Determines delivery characteristics.
 *
 * Ubiquitous language: BowlerType — not "bowling style" or "delivery type".
 * 4 types as confirmed by human (OQ-18).
 */
enum class BowlerType {
    FAST,
    MEDIUM_FAST,
    OFF_SPIN,
    LEG_SPIN
}
