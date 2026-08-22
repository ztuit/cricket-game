package com.cricketgame.domain.match

/**
 * Domain events emitted via Kotlin Flows (ADR-004).
 *
 * Each event carries a timestamp for structured logging (Ops requirement).
 * Events are collected via Match.collectEvents() for testing.
 */
sealed class DomainEvent {
    abstract val timestamp: Long

    data class MatchStarted(
        val matchId: String,
        val groundId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : DomainEvent()

    data class TossCompleted(
        val tossResult: TossResult,
        override val timestamp: Long = System.currentTimeMillis()
    ) : DomainEvent()
}
