package com.cricketgame.domain.pitch

/**
 * The prepared strip of ground where the ball bounces.
 *
 * Divided into zones, each with its own surface condition.
 * Ball age tracks number of deliveries bowled (affects swing/degradation).
 *
 * Ubiquitous language: Pitch — not "ground" (that's the venue) or "surface" (too vague).
 */
class Pitch private constructor(
    val pitchId: String,
    val groundId: String,
    val weather: Weather,
    val zones: List<SurfaceCondition>,
    val ballAge: Int
) {
    companion object {
        /**
         * Create a Pitch with zone grid and weather.
         *
         * @param pitchId Unique identifier
         * @param groundId Reference to the Ground this pitch belongs to
         * @param weather Match-level weather (constant for the match)
         * @param zones Surface conditions per zone
         */
        fun create(
            pitchId: String,
            groundId: String,
            weather: Weather,
            zones: List<SurfaceCondition>
        ): Pitch {
            return Pitch(
                pitchId = pitchId,
                groundId = groundId,
                weather = weather,
                zones = zones,
                ballAge = 0
            )
        }
    }
}
