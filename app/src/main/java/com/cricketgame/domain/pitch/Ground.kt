package com.cricketgame.domain.pitch

/**
 * The cricket venue where the match is played.
 *
 * Has a name, weather, and associated pitch.
 * Ground data is static (SE note).
 *
 * Ubiquitous language: Ground — not "venue" (acceptable synonym but prefer "ground").
 */
class Ground private constructor(
    val groundId: String,
    val name: String,
    val location: String,
    val defaultWeather: Weather
) {
    companion object {
        /**
         * Create a Ground with validated attributes.
         */
        fun create(
            groundId: String,
            name: String,
            location: String,
            defaultWeather: Weather
        ): Ground {
            return Ground(
                groundId = groundId,
                name = name,
                location = location,
                defaultWeather = defaultWeather
            )
        }
    }
}
