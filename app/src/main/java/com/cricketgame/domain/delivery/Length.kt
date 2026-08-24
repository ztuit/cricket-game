package com.cricketgame.domain.delivery

/**
 * Where the delivery pitches on the surface relative to the batsman.
 *
 * Ubiquitous language: Length — not "bounce point" or "pitch length".
 */
enum class Length {
    FULL,
    GOOD_LENGTH,
    SHORT,
    YORKER
}
