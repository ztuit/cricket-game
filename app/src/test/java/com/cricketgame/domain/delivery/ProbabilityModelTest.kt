package com.cricketgame.domain.delivery

import com.cricketgame.domain.player.BatsmanStats
import com.cricketgame.domain.player.BowlerStats
import com.cricketgame.domain.pitch.SurfaceCondition
import com.cricketgame.domain.pitch.Weather
import com.cricketgame.domain.pitch.WeatherCondition
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for the ProbabilityModel — the pure function that resolves outcomes.
 *
 * Ubiquitous language terms (from ddd.md):
 * - Outcome: The result of a delivery
 * - Probability model: Takes BallCharacteristics, ShotSelection, BowlerStats, BatsmanStats,
 *   SurfaceCondition, Weather, and ball age to produce an Outcome
 *
 * Architecture (ADR-005): The probability model is a pure function in domain.delivery.
 * It is deterministic given a seed — same inputs + seed = same outcome.
 * Randomness is injected via seed, not Math.random().
 *
 * Factors included in the model:
 * - BowlerStats (bowlingSkill, accuracy, variation, wideRate, noBallRate)
 * - BatsmanStats (battingSkill, timing, power, composure)
 * - SurfaceCondition (degradation, moisture, roughness)
 * - Weather (condition, temperature, humidity)
 * - Ball age (number of deliveries bowled)
 */
class ProbabilityModelTest {

    // ============================================================
    // Happy path tests — Determinism (ADR-005)
    // ============================================================

    @Test
    fun `should_resolve_outcome_deterministically_given_same_seed`() {
        val ballCharacteristics = createValidBallCharacteristics()
        val shotSelection = createValidShotSelection()
        val bowlerStats = createValidBowlerStats()
        val batsmanStats = createValidBatsmanStats()
        val surfaceCondition = createValidSurfaceCondition()
        val weather = createValidWeather()
        val seed = 42L

        val outcome1 = ProbabilityModel.resolve(
            ballCharacteristics = ballCharacteristics,
            shotSelection = shotSelection,
            bowlerStats = bowlerStats,
            batsmanStats = batsmanStats,
            surfaceCondition = surfaceCondition,
            weather = weather,
            ballAge = 0,
            seed = seed
        )

        val outcome2 = ProbabilityModel.resolve(
            ballCharacteristics = ballCharacteristics,
            shotSelection = shotSelection,
            bowlerStats = bowlerStats,
            batsmanStats = batsmanStats,
            surfaceCondition = surfaceCondition,
            weather = weather,
            ballAge = 0,
            seed = seed
        )

        assertEquals(
            "Same inputs and seed should produce identical outcome",
            outcome1, outcome2
        )
        assertEquals("Outcome type should be identical", outcome1.type, outcome2.type)
        assertEquals("Runs should be identical", outcome1.runs, outcome2.runs)
        assertEquals("DismissalType should be identical", outcome1.dismissalType, outcome2.dismissalType)
    }

    @Test
    fun `should_resolve_outcome_deterministically_across_multiple_calls`() {
        val inputs = createStandardInputs()
        val seed = 123L

        val outcomes = (1..100).map {
            ProbabilityModel.resolve(
                ballCharacteristics = inputs.first,
                shotSelection = inputs.second.first,
                bowlerStats = inputs.second.second.first,
                batsmanStats = inputs.second.second.second.first,
                surfaceCondition = inputs.second.second.second.second.first,
                weather = inputs.second.second.second.second.second,
                ballAge = 0,
                seed = seed
            )
        }

        val distinctOutcomes = outcomes.distinct()
        assertEquals(
            "100 calls with same seed should produce exactly 1 outcome",
            1, distinctOutcomes.size
        )
    }

    @Test
    fun `should_produce_different_outcomes_with_different_seeds`() {
        val ballCharacteristics = createValidBallCharacteristics()
        val shotSelection = createValidShotSelection()
        val bowlerStats = createValidBowlerStats()
        val batsmanStats = createValidBatsmanStats()
        val surfaceCondition = createValidSurfaceCondition()
        val weather = createValidWeather()

        val outcomes = (1L..50L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = ballCharacteristics,
                shotSelection = shotSelection,
                bowlerStats = bowlerStats,
                batsmanStats = batsmanStats,
                surfaceCondition = surfaceCondition,
                weather = weather,
                ballAge = 0,
                seed = seed
            )
        }

        val distinctTypes = outcomes.map { it.type }.distinct()
        assertTrue(
            "Different seeds should produce variety of outcome types, got: $distinctTypes",
            distinctTypes.size > 1
        )
    }

    // ============================================================
    // Happy path tests — All factors included
    // ============================================================

    @Test
    fun `should_include_BowlerStats_in_probability_calculation`() {
        // Different bowler skills should produce different outcome distributions
        val weakBowler = BowlerStats(0.2f, 0.2f, 0.2f, 0.3f, 0.2f)
        val strongBowler = BowlerStats(0.9f, 0.9f, 0.9f, 0.02f, 0.01f)

        val weakOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = weakBowler,
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = 0,
                seed = seed
            )
        }

        val strongOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = strongBowler,
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = 0,
                seed = seed
            )
        }

        val weakWickets = weakOutcomes.count { it.type == OutcomeType.WICKET }
        val strongWickets = strongOutcomes.count { it.type == OutcomeType.WICKET }

        // Strong bowler should take more wickets than weak bowler
        assertTrue(
            "Strong bowler ($strongWickets wickets) should take more wickets than weak bowler ($weakWickets wickets)",
            strongWickets > weakWickets
        )
    }

    @Test
    fun `should_include_BatsmanStats_in_probability_calculation`() {
        // Different batsman skills should produce different outcome distributions
        val weakBatsman = BatsmanStats(0.2f, 0.2f, 0.2f, 0.2f)
        val strongBatsman = BatsmanStats(0.9f, 0.9f, 0.9f, 0.9f)

        val weakOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = createValidBowlerStats(),
                batsmanStats = weakBatsman,
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = 0,
                seed = seed
            )
        }

        val strongOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = createValidBowlerStats(),
                batsmanStats = strongBatsman,
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = 0,
                seed = seed
            )
        }

        val weakRuns = weakOutcomes.sumOf { it.runs }
        val strongRuns = strongOutcomes.sumOf { it.runs }

        // Strong batsman should score more runs than weak batsman
        assertTrue(
            "Strong batsman ($strongRuns runs) should score more than weak batsman ($weakRuns runs)",
            strongRuns > weakRuns
        )
    }

    @Test
    fun `should_include_SurfaceCondition_in_probability_calculation`() {
        // Different surface conditions should affect outcomes
        val goodSurface = SurfaceCondition("zone-1", degradation = 0.0f, moisture = 0.5f, roughness = 0.0f)
        val degradedSurface = SurfaceCondition("zone-1", degradation = 0.9f, moisture = 0.2f, roughness = 0.8f)

        val goodOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = goodSurface,
                weather = createValidWeather(),
                ballAge = 0,
                seed = seed
            )
        }

        val degradedOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = degradedSurface,
                weather = createValidWeather(),
                ballAge = 0,
                seed = seed
            )
        }

        // Degraded surface should produce different distribution
        val goodWickets = goodOutcomes.count { it.type == OutcomeType.WICKET }
        val degradedWickets = degradedOutcomes.count { it.type == OutcomeType.WICKET }

        // At minimum, the distributions should differ
        assertTrue(
            "Surface condition should affect outcome distribution",
            goodWickets != degradedWickets || goodOutcomes.sumOf { it.runs } != degradedOutcomes.sumOf { it.runs }
        )
    }

    @Test
    fun `should_include_Weather_in_probability_calculation`() {
        // Different weather conditions should affect outcomes
        val sunny = Weather(WeatherCondition.SUNNY, 30.0f, 0.3f)
        val overcast = Weather(WeatherCondition.OVERCAST, 18.0f, 0.8f)

        val sunnyOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = sunny,
                ballAge = 0,
                seed = seed
            )
        }

        val overcastOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = overcast,
                ballAge = 0,
                seed = seed
            )
        }

        // Weather should affect the distribution
        val sunnyRuns = sunnyOutcomes.sumOf { it.runs }
        val overcastRuns = overcastOutcomes.sumOf { it.runs }

        assertTrue(
            "Weather should affect outcome distribution",
            sunnyRuns != overcastRuns || sunnyOutcomes.count { it.type == OutcomeType.WICKET } != overcastOutcomes.count { it.type == OutcomeType.WICKET }
        )
    }

    @Test
    fun `should_include_ball_age_in_probability_calculation`() {
        // Ball age (number of deliveries bowled) should affect swing/degradation
        val newBall = 0
        val oldBall = 100

        val newBallOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = newBall,
                seed = seed
            )
        }

        val oldBallOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = oldBall,
                seed = seed
            )
        }

        // Ball age should affect the distribution
        val newBallRuns = newBallOutcomes.sumOf { it.runs }
        val oldBallRuns = oldBallOutcomes.sumOf { it.runs }

        assertTrue(
            "Ball age should affect outcome distribution",
            newBallRuns != oldBallRuns || newBallOutcomes.count { it.type == OutcomeType.WICKET } != oldBallOutcomes.count { it.type == OutcomeType.WICKET }
        )
    }

    // ============================================================
    // Happy path tests — Shot suitability against ball characteristics
    // ============================================================

    @Test
    fun `should_weight_shot_suitability_against_ball_characteristics`() {
        // A pull shot against a short ball should be more effective than against a full ball
        val shortBall = BallCharacteristics(Line.OFF_STUMP, Length.SHORT, 0.8f, 0.0f)
        val fullBall = BallCharacteristics(Line.OFF_STUMP, Length.FULL, 0.8f, 0.0f)
        val pullShot = ShotSelection(ShotType.PULL, 0.0f)

        val shortBallOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = shortBall,
                shotSelection = pullShot,
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = 0,
                seed = seed
            )
        }

        val fullBallOutcomes = (1L..100L).map { seed ->
            ProbabilityModel.resolve(
                ballCharacteristics = fullBall,
                shotSelection = pullShot,
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = 0,
                seed = seed
            )
        }

        val shortBallRuns = shortBallOutcomes.sumOf { it.runs }
        val fullBallRuns = fullBallOutcomes.sumOf { it.runs }

        // Pull shot should be more effective against short ball
        assertTrue(
            "Pull shot should score more against short ball ($shortBallRuns) than full ball ($fullBallRuns)",
            shortBallRuns >= fullBallRuns
        )
    }

    // ============================================================
    // Happy path tests — Outcome validity
    // ============================================================

    @Test
    fun `should_always_produce_valid_Outcome_with_non_negative_runs`() {
        // Test with many different seeds to ensure all outcomes are valid
        for (seed in 1L..200L) {
            val outcome = ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = 0,
                seed = seed
            )

            assertTrue(
                "Runs should be non-negative for seed $seed",
                outcome.runs >= 0
            )
        }
    }

    @Test
    fun `should_produce_Wicket_outcome_with_dismissalType_set`() {
        // Find a seed that produces a wicket
        for (seed in 1L..1000L) {
            val outcome = ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = 0,
                seed = seed
            )

            if (outcome.type == OutcomeType.WICKET) {
                assertNotNull(
                    "Wicket outcome must have dismissalType",
                    outcome.dismissalType
                )
                return // Test passed
            }
        }

        // If we get here, we didn't find a wicket in 1000 seeds — that's suspicious
        fail("Should have produced at least one Wicket outcome in 1000 seeds")
    }

    @Test
    fun `should_produce_Wide_and_NoBall_outcomes_with_correct_runs`() {
        // Find seeds that produce Wide and NoBall
        var foundWide = false
        var foundNoBall = false

        for (seed in 1L..1000L) {
            val outcome = ProbabilityModel.resolve(
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = createValidShotSelection(),
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = 0,
                seed = seed
            )

            if (outcome.type == OutcomeType.WIDE) {
                assertEquals("Wide should award 1 run", 1, outcome.runs)
                assertFalse("Wide should not be legal", outcome.isLegal)
                foundWide = true
            }

            if (outcome.type == OutcomeType.NO_BALL) {
                assertEquals("NoBall should award 1 run", 1, outcome.runs)
                assertFalse("NoBall should not be legal", outcome.isLegal)
                foundNoBall = true
            }

            if (foundWide && foundNoBall) break
        }

        assertTrue("Should have found at least one Wide outcome", foundWide)
        assertTrue("Should have found at least one NoBall outcome", foundNoBall)
    }

    // ============================================================
    // Edge case tests
    // ============================================================

    @Test
    fun `should_resolve_outcome_with_seed_at_zero`() {
        val outcome = ProbabilityModel.resolve(
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection(),
            bowlerStats = createValidBowlerStats(),
            batsmanStats = createValidBatsmanStats(),
            surfaceCondition = createValidSurfaceCondition(),
            weather = createValidWeather(),
            ballAge = 0,
            seed = 0L
        )

        assertNotNull("Outcome should not be null with seed 0", outcome)
        assertTrue("Runs should be non-negative", outcome.runs >= 0)
    }

    @Test
    fun `should_resolve_outcome_with_seed_at_max_long_value`() {
        val outcome = ProbabilityModel.resolve(
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection(),
            bowlerStats = createValidBowlerStats(),
            batsmanStats = createValidBatsmanStats(),
            surfaceCondition = createValidSurfaceCondition(),
            weather = createValidWeather(),
            ballAge = 0,
            seed = Long.MAX_VALUE
        )

        assertNotNull("Outcome should not be null with max seed", outcome)
        assertTrue("Runs should be non-negative", outcome.runs >= 0)
    }

    @Test
    fun `should_resolve_outcome_with_negative_seed`() {
        val outcome = ProbabilityModel.resolve(
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection(),
            bowlerStats = createValidBowlerStats(),
            batsmanStats = createValidBatsmanStats(),
            surfaceCondition = createValidSurfaceCondition(),
            weather = createValidWeather(),
            ballAge = 0,
            seed = -42L
        )

        assertNotNull("Outcome should not be null with negative seed", outcome)
        assertTrue("Runs should be non-negative", outcome.runs >= 0)
    }

    @Test
    fun `should_resolve_outcome_with_all_bowlerStats_at_zero`() {
        val zeroBowlerStats = BowlerStats(0.0f, 0.0f, 0.0f, 0.0f, 0.0f)

        val outcome = ProbabilityModel.resolve(
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection(),
            bowlerStats = zeroBowlerStats,
            batsmanStats = createValidBatsmanStats(),
            surfaceCondition = createValidSurfaceCondition(),
            weather = createValidWeather(),
            ballAge = 0,
            seed = 42L
        )

        assertNotNull("Outcome should not be null with zero bowler stats", outcome)
        assertTrue("Runs should be non-negative", outcome.runs >= 0)
    }

    @Test
    fun `should_resolve_outcome_with_all_batsmanStats_at_zero`() {
        val zeroBatsmanStats = BatsmanStats(0.0f, 0.0f, 0.0f, 0.0f)

        val outcome = ProbabilityModel.resolve(
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection(),
            bowlerStats = createValidBowlerStats(),
            batsmanStats = zeroBatsmanStats,
            surfaceCondition = createValidSurfaceCondition(),
            weather = createValidWeather(),
            ballAge = 0,
            seed = 42L
        )

        assertNotNull("Outcome should not be null with zero batsman stats", outcome)
        assertTrue("Runs should be non-negative", outcome.runs >= 0)
    }

    @Test
    fun `should_resolve_outcome_with_surfaceCondition_at_maximum_degradation`() {
        val maxDegradation = SurfaceCondition("zone-1", degradation = 1.0f, moisture = 0.0f, roughness = 1.0f)

        val outcome = ProbabilityModel.resolve(
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection(),
            bowlerStats = createValidBowlerStats(),
            batsmanStats = createValidBatsmanStats(),
            surfaceCondition = maxDegradation,
            weather = createValidWeather(),
            ballAge = 0,
            seed = 42L
        )

        assertNotNull("Outcome should not be null with max degradation", outcome)
        assertTrue("Runs should be non-negative", outcome.runs >= 0)
    }

    @Test
    fun `should_resolve_outcome_with_very_high_ball_age`() {
        val outcome = ProbabilityModel.resolve(
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection(),
            bowlerStats = createValidBowlerStats(),
            batsmanStats = createValidBatsmanStats(),
            surfaceCondition = createValidSurfaceCondition(),
            weather = createValidWeather(),
            ballAge = 120, // Full T20 innings
            seed = 42L
        )

        assertNotNull("Outcome should not be null with high ball age", outcome)
        assertTrue("Runs should be non-negative", outcome.runs >= 0)
    }

    // ============================================================
    // Helper methods
    // ============================================================

    private fun createValidBallCharacteristics(): BallCharacteristics {
        return BallCharacteristics(
            line = Line.OFF_STUMP,
            length = Length.GOOD_LENGTH,
            pace = 0.7f,
            spin = 0.3f
        )
    }

    private fun createValidShotSelection(): ShotSelection {
        return ShotSelection(
            shotType = ShotType.DRIVE,
            wristAngle = 45.0f
        )
    }

    private fun createValidBowlerStats(): BowlerStats {
        return BowlerStats(
            bowlingSkill = 0.8f,
            accuracy = 0.7f,
            variation = 0.6f,
            wideRate = 0.05f,
            noBallRate = 0.03f
        )
    }

    private fun createValidBatsmanStats(): BatsmanStats {
        return BatsmanStats(
            battingSkill = 0.75f,
            timing = 0.7f,
            power = 0.65f,
            composure = 0.8f
        )
    }

    private fun createValidSurfaceCondition(): SurfaceCondition {
        return SurfaceCondition(
            zoneId = "zone-1",
            degradation = 0.2f,
            moisture = 0.5f,
            roughness = 0.1f
        )
    }

    private fun createValidWeather(): Weather {
        return Weather(
            condition = WeatherCondition.SUNNY,
            temperature = 25.0f,
            humidity = 0.4f
        )
    }

    private fun createStandardInputs():
        Pair<BallCharacteristics, Pair<ShotSelection, Pair<BowlerStats, Pair<BatsmanStats, Pair<SurfaceCondition, Weather>>>>> {
        return Pair(
            createValidBallCharacteristics(),
            Pair(
                createValidShotSelection(),
                Pair(
                    createValidBowlerStats(),
                    Pair(
                        createValidBatsmanStats(),
                        Pair(
                            createValidSurfaceCondition(),
                            createValidWeather()
                        )
                    )
                )
            )
        )
    }
}
