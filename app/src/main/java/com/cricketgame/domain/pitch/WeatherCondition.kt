package com.cricketgame.domain.pitch

/**
 * Match-level atmospheric condition. Affects pitch degradation.
 *
 * Ubiquitous language: Weather — not "climate" or "atmospheric state".
 */
enum class WeatherCondition {
    SUNNY,
    OVERCAST,
    HUMID,
    CLOUDY
}
