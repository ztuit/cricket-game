package com.cricketgame.domain.delivery

import com.cricketgame.domain.player.BatsmanStats
import com.cricketgame.domain.player.BowlerStats
import com.cricketgame.domain.pitch.SurfaceCondition
import com.cricketgame.domain.pitch.Weather
import com.cricketgame.domain.pitch.WeatherCondition
import kotlin.random.Random

/**
 * Pure function that resolves delivery outcomes based on all game factors.
 *
 * Architecture (ADR-005): Deterministic given a seed — same inputs + seed = same outcome.
 * Randomness injected via seed, not Math.random().
 *
 * Factors included:
 * - BowlerStats (bowlingSkill, accuracy, variation, wideRate, noBallRate)
 * - BatsmanStats (battingSkill, timing, power, composure)
 * - SurfaceCondition (degradation, moisture, roughness)
 * - Weather (condition, temperature, humidity)
 * - Ball age (number of deliveries bowled)
 * - Shot suitability against ball characteristics
 */
object ProbabilityModel {

    /**
     * Resolve the outcome of a delivery given all game factors.
     *
     * @return a valid Outcome with non-negative runs, dismissalType set for wickets,
     *         and correct isLegal flag for wides/no-balls.
     */
    fun resolve(
        ballCharacteristics: BallCharacteristics,
        shotSelection: ShotSelection,
        bowlerStats: BowlerStats,
        batsmanStats: BatsmanStats,
        surfaceCondition: SurfaceCondition,
        weather: Weather,
        ballAge: Int,
        seed: Long
    ): Outcome {
        val random = Random(seed)

        // Step 1: Check for illegal deliveries (Wide, NoBall)
        val wideRoll = random.nextFloat()
        if (wideRoll < bowlerStats.wideRate) {
            return Outcome(OutcomeType.WIDE, 1, null)
        }

        val noBallRoll = random.nextFloat()
        if (noBallRoll < bowlerStats.noBallRate) {
            return Outcome(OutcomeType.NO_BALL, 1, null)
        }

        // Step 2: Calculate shot suitability against ball characteristics
        val suitability = getShotSuitability(shotSelection.shotType, ballCharacteristics.length)

        // Step 3: Calculate skill factors
        val bowlerSkill = (bowlerStats.bowlingSkill + bowlerStats.accuracy) / 2.0f
        val batsmanSkill = (batsmanStats.battingSkill + batsmanStats.timing + batsmanStats.composure) / 3.0f

        // Step 4: Environmental modifiers
        val surfaceEffect = 1.0f - surfaceCondition.degradation * 0.3f +
                surfaceCondition.moisture * 0.1f - surfaceCondition.roughness * 0.1f
        val weatherEffect = getWeatherEffect(weather)
        val ageEffect = 1.0f - (ballAge.coerceIn(0, 120) / 120.0f) * 0.2f

        // Step 5: Calculate batting power and net advantage
        val battingPower = suitability * batsmanSkill * surfaceEffect * weatherEffect * ageEffect
        val netAdvantage = battingPower - bowlerSkill

        // Step 6: Determine outcome type
        val outcomeRoll = random.nextFloat()

        // Wicket probability: higher when bowler has advantage
        val wicketProb = if (netAdvantage < 0) {
            (0.15f + (-netAdvantage) * 0.3f).coerceAtMost(0.4f)
        } else {
            (0.05f - netAdvantage * 0.05f).coerceAtLeast(0.02f)
        }

        if (outcomeRoll < wicketProb) {
            val dismissalType = determineDismissalType(random)
            return Outcome(OutcomeType.WICKET, 0, dismissalType)
        }

        // Step 7: Determine runs scored
        val runs = determineRuns(random, battingPower, batsmanStats.power, suitability)

        return if (runs == 0) {
            Outcome(OutcomeType.DOT_BALL, 0, null)
        } else {
            Outcome(OutcomeType.RUNS, runs, null)
        }
    }

    /**
     * Shot suitability matrix: how effective each shot type is against each length.
     * Higher values = better chance of scoring.
     *
     * Based on cricket mechanics:
     * - Pull/Cut/UpperCut are best against Short deliveries
     * - Drive/Sweep/LegGlance are best against Full deliveries
     * - Defensive/Leave work across all lengths
     * - Yorker is hardest to score off for most shots
     */
    private fun getShotSuitability(shotType: ShotType, length: Length): Float {
        return when (length) {
            Length.FULL -> when (shotType) {
                ShotType.DRIVE -> 0.9f
                ShotType.DEFENSIVE -> 0.7f
                ShotType.LEAVE -> 0.5f
                ShotType.LEG_GLANCE -> 0.6f
                ShotType.PULL -> 0.2f
                ShotType.CUT -> 0.3f
                ShotType.SWEEP -> 0.7f
                ShotType.SLOG -> 0.5f
                ShotType.REVERSE_SWEEP -> 0.4f
                ShotType.UPPER_CUT -> 0.1f
            }
            Length.GOOD_LENGTH -> when (shotType) {
                ShotType.DRIVE -> 0.6f
                ShotType.DEFENSIVE -> 0.8f
                ShotType.LEAVE -> 0.6f
                ShotType.LEG_GLANCE -> 0.5f
                ShotType.PULL -> 0.4f
                ShotType.CUT -> 0.5f
                ShotType.SWEEP -> 0.5f
                ShotType.SLOG -> 0.4f
                ShotType.REVERSE_SWEEP -> 0.3f
                ShotType.UPPER_CUT -> 0.2f
            }
            Length.SHORT -> when (shotType) {
                ShotType.DRIVE -> 0.3f
                ShotType.DEFENSIVE -> 0.5f
                ShotType.LEAVE -> 0.7f
                ShotType.LEG_GLANCE -> 0.3f
                ShotType.PULL -> 0.9f
                ShotType.CUT -> 0.8f
                ShotType.SWEEP -> 0.2f
                ShotType.SLOG -> 0.7f
                ShotType.REVERSE_SWEEP -> 0.2f
                ShotType.UPPER_CUT -> 0.8f
            }
            Length.YORKER -> when (shotType) {
                ShotType.DRIVE -> 0.3f
                ShotType.DEFENSIVE -> 0.6f
                ShotType.LEAVE -> 0.4f
                ShotType.LEG_GLANCE -> 0.3f
                ShotType.PULL -> 0.1f
                ShotType.CUT -> 0.1f
                ShotType.SWEEP -> 0.3f
                ShotType.SLOG -> 0.2f
                ShotType.REVERSE_SWEEP -> 0.2f
                ShotType.UPPER_CUT -> 0.1f
            }
        }
    }

    /**
     * Weather effect on batting conditions.
     * Overcast/humid conditions help bowlers (swing), reducing batting effectiveness.
     */
    private fun getWeatherEffect(weather: Weather): Float {
        return when (weather.condition) {
            WeatherCondition.SUNNY -> 1.0f
            WeatherCondition.OVERCAST -> 0.85f
            WeatherCondition.HUMID -> 0.9f
            WeatherCondition.CLOUDY -> 0.9f
        }
    }

    /**
     * Determine dismissal type based on ball and shot characteristics.
     * Distribution: Bowled (30%), Caught (30%), LBW (25%), Stumped (15%).
     */
    private fun determineDismissalType(random: Random): DismissalType {
        val roll = random.nextFloat()
        return when {
            roll < 0.30f -> DismissalType.BOWLED
            roll < 0.60f -> DismissalType.CAUGHT
            roll < 0.85f -> DismissalType.LBW
            else -> DismissalType.STUMPED
        }
    }

    /**
     * Determine runs scored based on batting power and shot suitability.
     * Higher batting power = more runs on average.
     * Power stat required for sixes.
     */
    private fun determineRuns(
        random: Random,
        battingPower: Float,
        power: Float,
        suitability: Float
    ): Int {
        val roll = random.nextFloat()
        val score = (battingPower * suitability).coerceIn(0.0f, 1.0f)

        // Thresholds shift lower with higher score = more runs
        val dotThreshold = 0.4f - score * 0.3f
        val singleThreshold = 0.6f - score * 0.2f
        val twoThreshold = 0.75f - score * 0.15f
        val fourThreshold = 0.85f - score * 0.1f

        return when {
            roll < dotThreshold -> 0
            roll < singleThreshold -> 1
            roll < twoThreshold -> 2
            roll < fourThreshold -> 4
            power > 0.5f -> 6
            else -> 4
        }
    }
}
