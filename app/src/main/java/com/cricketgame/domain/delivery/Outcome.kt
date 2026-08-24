package com.cricketgame.domain.delivery

/**
 * The result of a delivery: runs scored, dismissal type, wide, no ball.
 *
 * Ubiquitous language: Outcome — not "result" or "delivery result".
 *
 * Invariants:
 * - If type=Wicket, dismissalType must be set
 * - If type!=Wicket, dismissalType must be null
 * - Runs >= 0
 * - Wide and NoBall are illegal deliveries (isLegal = false)
 *
 * RunOut excluded for single batsman model (ADR-007).
 */
data class Outcome(
    val type: OutcomeType,
    val runs: Int,
    val dismissalType: DismissalType?
) {
    /**
     * Whether this delivery is legal. Wide and NoBall are illegal —
     * they award 1 run and the ball is re-bowled.
     */
    val isLegal: Boolean
        get() = type != OutcomeType.WIDE && type != OutcomeType.NO_BALL

    init {
        require(runs >= 0) { "runs must be non-negative, was $runs" }
        if (type == OutcomeType.WICKET) {
            requireNotNull(dismissalType) { "dismissalType must be set when type is Wicket" }
        }
        if (type != OutcomeType.WICKET) {
            require(dismissalType == null) { "dismissalType must be null when type is $type" }
        }
    }
}
