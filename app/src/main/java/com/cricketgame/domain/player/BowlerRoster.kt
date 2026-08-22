package com.cricketgame.domain.player

/**
 * A collection of bowlers covering all 4 BowlerTypes.
 *
 * At least 5 bowlers required to ensure coverage of Fast, MediumFast, OffSpin, LegSpin.
 * Ubiquitous language: BowlerRoster — not "bowling lineup" or "bowler list".
 */
class BowlerRoster private constructor(
    val bowlers: List<Bowler>
) {
    /**
     * Check if this roster covers all 4 BowlerTypes.
     */
    fun coversBowlerType(type: BowlerType): Boolean {
        return bowlers.any { it.bowlerType == type }
    }

    companion object {
        /**
         * Create a BowlerRoster with validation.
         *
         * @param bowlers At least 5 bowlers covering all 4 BowlerTypes
         * @throws IllegalArgumentException if fewer than 5 bowlers or a BowlerType is missing
         */
        fun create(bowlers: List<Bowler>): BowlerRoster {
            require(bowlers.size >= 5) { "BowlerRoster must have at least 5 bowlers, had ${bowlers.size}" }

            val coveredTypes = bowlers.map { it.bowlerType }.toSet()
            val missingTypes = BowlerType.values().toSet() - coveredTypes
            require(missingTypes.isEmpty()) {
                "BowlerRoster must cover all BowlerTypes. Missing: $missingTypes"
            }

            return BowlerRoster(bowlers = bowlers)
        }
    }
}
