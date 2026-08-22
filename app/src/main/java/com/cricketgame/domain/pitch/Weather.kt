package com.cricketgame.domain.pitch

/**
 * Weather value object for match-level atmospheric conditions.
 *
 * Humidity must be in [0, 1] (SEC-003 validation).
 * Ubiquitous language: Weather.
 */
data class Weather(
    val condition: WeatherCondition,
    val temperature: Float,
    val humidity: Float
) {
    init {
        require(humidity in 0.0f..1.0f) { "humidity must be in [0,1], was $humidity" }
    }
}
