package com.cricketgame.domain.field

/**
 * A fielder's position on the ground.
 *
 * Ubiquitous language: FielderPosition — positionName, x, y coordinates.
 * Coordinates are normalized to [-1, 1] range.
 */
data class FielderPosition(
    val positionName: String,
    val x: Float,
    val y: Float
)
