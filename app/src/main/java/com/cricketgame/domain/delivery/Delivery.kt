package com.cricketgame.domain.delivery

import com.cricketgame.domain.match.DomainEvent
import com.cricketgame.domain.player.BatsmanStats
import com.cricketgame.domain.player.BowlerStats
import com.cricketgame.domain.pitch.SurfaceCondition
import com.cricketgame.domain.pitch.Weather

/**
 * Delivery aggregate — the atomic unit of gameplay.
 *
 * A single ball bowled from the bowler to the batsman.
 * Pure function: inputs in, Outcome out (ADR-005).
 *
 * Ubiquitous language: Delivery — not "ball" (ambiguous) or "delivery event".
 *
 * References by data (not by holding state):
 * - BowlerStats (player context)
 * - BatsmanStats (player context)
 * - SurfaceCondition (pitch context)
 * - Weather (pitch context)
 *
 * This is the cross-context dependency point in the bounded context map.
 */
class Delivery private constructor(
    val deliveryId: String,
    val deliveryNumber: Int,
    val ballCharacteristics: BallCharacteristics,
    val shotSelection: ShotSelection
) {
    private val events = mutableListOf<DomainEvent>()

    /**
     * Resolve the outcome of this delivery given all game factors.
     *
     * Delegates to ProbabilityModel for the actual calculation.
     * Emits OutcomeResolved event.
     *
     * @return the Outcome of this delivery
     */
    fun resolveOutcome(
        bowlerStats: BowlerStats,
        batsmanStats: BatsmanStats,
        surfaceCondition: SurfaceCondition,
        weather: Weather,
        ballAge: Int,
        seed: Long
    ): Outcome {
        val outcome = ProbabilityModel.resolve(
            ballCharacteristics = ballCharacteristics,
            shotSelection = shotSelection,
            bowlerStats = bowlerStats,
            batsmanStats = batsmanStats,
            surfaceCondition = surfaceCondition,
            weather = weather,
            ballAge = ballAge,
            seed = seed
        )

        events.add(
            DomainEvent.OutcomeResolved(
                outcome = outcome,
                runsScored = outcome.runs,
                isWicket = outcome.type == OutcomeType.WICKET
            )
        )

        return outcome
    }

    /**
     * Collect and clear all accumulated domain events.
     *
     * Used for testing and event forwarding.
     * Events are cleared after collection to prevent duplicate processing.
     */
    fun collectEvents(): List<DomainEvent> {
        val collected = events.toList()
        events.clear()
        return collected
    }

    companion object {
        /**
         * Factory method to create a Delivery.
         *
         * Validates inputs and emits DeliveryBowled and ShotPlayed events.
         *
         * @throws IllegalArgumentException if deliveryId is empty or deliveryNumber is negative
         */
        fun create(
            deliveryId: String,
            deliveryNumber: Int,
            ballCharacteristics: BallCharacteristics,
            shotSelection: ShotSelection
        ): Delivery {
            require(deliveryId.isNotEmpty()) { "deliveryId must not be empty" }
            require(deliveryNumber >= 0) { "deliveryNumber must be non-negative, was $deliveryNumber" }

            val delivery = Delivery(
                deliveryId = deliveryId,
                deliveryNumber = deliveryNumber,
                ballCharacteristics = ballCharacteristics,
                shotSelection = shotSelection
            )

            delivery.events.add(
                DomainEvent.DeliveryBowled(
                    deliveryId = deliveryId,
                    ballCharacteristics = ballCharacteristics
                )
            )

            delivery.events.add(
                DomainEvent.ShotPlayed(
                    shotSelection = shotSelection
                )
            )

            return delivery
        }
    }
}
