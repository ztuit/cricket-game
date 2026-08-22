package com.cricketgame.domain.pitch

/**
 * The state of a zone on the pitch. Affects how the ball bounces.
 *
 * Degraded by deliveries, weather, and time.
 * All float attributes must be in [0, 1] (SEC-003 validation).
 *
 * Ubiquitous language: SurfaceCondition — not "coefficient" (jargon, Customer flagged).
 */
data class SurfaceCondition(
    val zoneId: String,
    val degradation: Float,
    val moisture: Float,
    val roughness: Float
) {
    init {
        require(degradation in 0.0f..1.0f) { "degradation must be in [0,1], was $degradation" }
        require(moisture in 0.0f..1.0f) { "moisture must be in [0,1], was $moisture" }
        require(roughness in 0.0f..1.0f) { "roughness must be in [0,1], was $roughness" }
    }
}
