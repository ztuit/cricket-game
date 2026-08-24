package com.cricketgame.domain.delivery

/**
 * Category of shot the batsman plays. 10 types covering:
 * - Vertical-bat: Drive, Defensive, Leave, LegGlance
 * - Horizontal-bat: Pull, Cut, Sweep
 * - Unorthodox: Slog, ReverseSweep, UpperCut
 *
 * Ubiquitous language: ShotType — not "shot selection" (that's the value object).
 */
enum class ShotType {
    DRIVE,
    PULL,
    CUT,
    SWEEP,
    DEFENSIVE,
    LEAVE,
    LEG_GLANCE,
    SLOG,
    REVERSE_SWEEP,
    UPPER_CUT
}
