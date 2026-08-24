package com.cricketgame.domain.match

import com.cricketgame.domain.delivery.BallCharacteristics
import com.cricketgame.domain.delivery.Outcome
import com.cricketgame.domain.delivery.ShotSelection

/**
 * Domain events emitted via Kotlin Flows (ADR-004).
 *
 * Each event carries a timestamp for structured logging (Ops requirement).
 * Events are collected via collectEvents() for testing.
 */
sealed class DomainEvent {
    abstract val timestamp: Long

    // Match lifecycle events
    data class MatchStarted(
        val matchId: String,
        val groundId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : DomainEvent()

    data class TossCompleted(
        val tossResult: TossResult,
        override val timestamp: Long = System.currentTimeMillis()
    ) : DomainEvent()

    // Delivery events (INCR-002)
    data class DeliveryBowled(
        val deliveryId: String,
        val ballCharacteristics: BallCharacteristics,
        override val timestamp: Long = System.currentTimeMillis()
    ) : DomainEvent()

    data class ShotPlayed(
        val shotSelection: ShotSelection,
        override val timestamp: Long = System.currentTimeMillis()
    ) : DomainEvent()

    data class OutcomeResolved(
        val outcome: Outcome,
        val runsScored: Int,
        val isWicket: Boolean,
        override val timestamp: Long = System.currentTimeMillis()
    ) : DomainEvent()
}
