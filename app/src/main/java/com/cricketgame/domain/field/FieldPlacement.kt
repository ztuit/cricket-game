package com.cricketgame.domain.field

import com.cricketgame.domain.player.BowlerType

/**
 * The arrangement of fielders on the ground.
 *
 * Exactly 11 fielding positions per placement (including bowler and wicket-keeper).
 * Field Placement changes between overs (bowler's tactical choice), not within an over.
 *
 * Ubiquitous language: FieldPlacement — not "field set" (too technical) or "field arrangement".
 */
class FieldPlacement private constructor(
    val placementId: String,
    val name: String,
    val bowlerType: BowlerType,
    val fielders: List<FielderPosition>
) {
    companion object {
        /**
         * Create a FieldPlacement with validated fielder count.
         *
         * @param placementId Unique identifier
         * @param name Descriptive name (e.g., "Attacking", "Defensive")
         * @param bowlerType Which bowler type this placement suits
         * @param fielders Exactly 11 fielder positions
         * @throws IllegalArgumentException if fielder count is not exactly 11
         */
        fun create(
            placementId: String,
            name: String,
            bowlerType: BowlerType,
            fielders: List<FielderPosition>
        ): FieldPlacement {
            require(fielders.size == 11) { "FieldPlacement must have exactly 11 fielders, had ${fielders.size}" }

            return FieldPlacement(
                placementId = placementId,
                name = name,
                bowlerType = bowlerType,
                fielders = fielders
            )
        }
    }
}
