package com.cricketgame.domain.delivery

/**
 * How the batsman gets out. 4 types for single batsman model (ADR-007).
 * RunOut excluded — requires partnership model.
 *
 * Ubiquitous language: DismissalType — not "out type" or "wicket type".
 */
enum class DismissalType {
    BOWLED,
    CAUGHT,
    LBW,
    STUMPED
}
