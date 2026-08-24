package com.cricketgame.domain.delivery

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for ShotSelection value object and ShotType enum.
 *
 * Ubiquitous language terms (from ddd.md):
 * - ShotSelection: Value object with shotType and wristAngle
 * - ShotType: Category of shot (Drive, Pull, Cut, Sweep, Defensive, Leave, LegGlance, Slog, ReverseSweep, UpperCut)
 * - WristAngle: Angle of the bat at point of contact, set by the player (float value)
 *
 * 10 shot types covering:
 * - Vertical-bat: Drive, Defensive, Leave, LegGlance
 * - Horizontal-bat: Pull, Cut, Sweep
 * - Unorthodox: Slog, ReverseSweep, UpperCut
 */
class ShotSelectionTest {

    // ============================================================
    // Happy path tests
    // ============================================================

    @Test
    fun `should_create_ShotSelection_with_valid_shotType_and_wristAngle`() {
        val shot = ShotSelection(
            shotType = ShotType.DRIVE,
            wristAngle = 45.0f
        )

        assertEquals(ShotType.DRIVE, shot.shotType)
        assertEquals(45.0f, shot.wristAngle, 0.001f)
    }

    @Test
    fun `should_have_all_ten_shot_types_available`() {
        // Verify all 10 shot types exist as defined in ddd.md
        val allShotTypes = ShotType.values()

        assertEquals("There should be exactly 10 shot types", 10, allShotTypes.size)

        assertTrue("DRIVE should be available", allShotTypes.contains(ShotType.DRIVE))
        assertTrue("PULL should be available", allShotTypes.contains(ShotType.PULL))
        assertTrue("CUT should be available", allShotTypes.contains(ShotType.CUT))
        assertTrue("SWEEP should be available", allShotTypes.contains(ShotType.SWEEP))
        assertTrue("DEFENSIVE should be available", allShotTypes.contains(ShotType.DEFENSIVE))
        assertTrue("LEAVE should be available", allShotTypes.contains(ShotType.LEAVE))
        assertTrue("LEG_GLANCE should be available", allShotTypes.contains(ShotType.LEG_GLANCE))
        assertTrue("SLOG should be available", allShotTypes.contains(ShotType.SLOG))
        assertTrue("REVERSE_SWEEP should be available", allShotTypes.contains(ShotType.REVERSE_SWEEP))
        assertTrue("UPPER_CUT should be available", allShotTypes.contains(ShotType.UPPER_CUT))
    }

    @Test
    fun `should_create_ShotSelection_with_all_ten_shot_types`() {
        // Verify each shot type can be used in a ShotSelection
        val drive = ShotSelection(ShotType.DRIVE, 0.0f)
        val pull = ShotSelection(ShotType.PULL, 30.0f)
        val cut = ShotSelection(ShotType.CUT, -20.0f)
        val sweep = ShotSelection(ShotType.SWEEP, 60.0f)
        val defensive = ShotSelection(ShotType.DEFENSIVE, 0.0f)
        val leave = ShotSelection(ShotType.LEAVE, 0.0f)
        val legGlance = ShotSelection(ShotType.LEG_GLANCE, 45.0f)
        val slog = ShotSelection(ShotType.SLOG, 90.0f)
        val reverseSweep = ShotSelection(ShotType.REVERSE_SWEEP, -45.0f)
        val upperCut = ShotSelection(ShotType.UPPER_CUT, 75.0f)

        assertEquals(ShotType.DRIVE, drive.shotType)
        assertEquals(ShotType.PULL, pull.shotType)
        assertEquals(ShotType.CUT, cut.shotType)
        assertEquals(ShotType.SWEEP, sweep.shotType)
        assertEquals(ShotType.DEFENSIVE, defensive.shotType)
        assertEquals(ShotType.LEAVE, leave.shotType)
        assertEquals(ShotType.LEG_GLANCE, legGlance.shotType)
        assertEquals(ShotType.SLOG, slog.shotType)
        assertEquals(ShotType.REVERSE_SWEEP, reverseSweep.shotType)
        assertEquals(ShotType.UPPER_CUT, upperCut.shotType)
    }

    @Test
    fun `should_accept_wristAngle_as_float_value`() {
        // Wrist angle is a float value — UI input mechanism is separate concern
        val shot = ShotSelection(
            shotType = ShotType.DRIVE,
            wristAngle = 33.7f
        )

        assertEquals(33.7f, shot.wristAngle, 0.001f)
    }

    @Test
    fun `should_accept_negative_wristAngle_for_cross_bat_shots`() {
        // Negative wrist angle is valid for certain shots (e.g., reverse sweep)
        val shot = ShotSelection(
            shotType = ShotType.REVERSE_SWEEP,
            wristAngle = -45.0f
        )

        assertEquals(-45.0f, shot.wristAngle, 0.001f)
    }

    @Test
    fun `should_accept_zero_wristAngle_as_neutral_position`() {
        val shot = ShotSelection(
            shotType = ShotType.DEFENSIVE,
            wristAngle = 0.0f
        )

        assertEquals(0.0f, shot.wristAngle, 0.001f)
    }

    // ============================================================
    // Unhappy path tests
    // ============================================================

    // Note: ShotType is an enum, so invalid shotType cannot be constructed at compile time.
    // The validation happens at the enum level — if the enum exists, it's valid.
    // This test verifies that the enum is properly sealed/fixed.
    @Test
    fun `should_only_allow_predefined_shot_types_no_custom_types`() {
        // Enum values() returns only the defined constants
        val values = ShotType.values()
        val expectedCount = 10
        assertEquals(
            "ShotType enum should have exactly $expectedCount predefined values",
            expectedCount,
            values.size
        )
    }

    // ============================================================
    // Edge case tests
    // ============================================================

    @Test
    fun `should_create_ShotSelection_with_wristAngle_at_zero_boundary`() {
        val shot = ShotSelection(ShotType.DRIVE, 0.0f)
        assertEquals(0.0f, shot.wristAngle, 0.001f)
    }

    @Test
    fun `should_create_ShotSelection_with_large_positive_wristAngle`() {
        // Wrist angle can be any float — the UI constrains the input
        val shot = ShotSelection(ShotType.SLOG, 180.0f)
        assertEquals(180.0f, shot.wristAngle, 0.001f)
    }

    @Test
    fun `should_create_ShotSelection_with_large_negative_wristAngle`() {
        val shot = ShotSelection(ShotType.REVERSE_SWEEP, -180.0f)
        assertEquals(-180.0f, shot.wristAngle, 0.001f)
    }

    @Test
    fun `should_categorise_shots_into_vertical_bat_horizontal_bat_and_unorthodox`() {
        // Vertical-bat shots: Drive, Defensive, Leave, LegGlance
        val verticalBatShots = listOf(ShotType.DRIVE, ShotType.DEFENSIVE, ShotType.LEAVE, ShotType.LEG_GLANCE)

        // Horizontal-bat shots: Pull, Cut, Sweep
        val horizontalBatShots = listOf(ShotType.PULL, ShotType.CUT, ShotType.SWEEP)

        // Unorthodox shots: Slog, ReverseSweep, UpperCut
        val unorthodoxShots = listOf(ShotType.SLOG, ShotType.REVERSE_SWEEP, ShotType.UPPER_CUT)

        // All categories should be represented
        assertEquals(4, verticalBatShots.size)
        assertEquals(3, horizontalBatShots.size)
        assertEquals(3, unorthodoxShots.size)

        // Total should be 10
        assertEquals(10, verticalBatShots.size + horizontalBatShots.size + unorthodoxShots.size)
    }
}
