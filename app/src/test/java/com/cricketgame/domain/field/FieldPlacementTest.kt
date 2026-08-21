package com.cricketgame.domain.field

import com.cricketgame.domain.player.BowlerType
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for Field Placement domain object.
 *
 * Ubiquitous language terms (from ddd.md):
 * - FieldPlacement: Arrangement of fielders on the ground
 * - Fielder: Non-bowling member of the bowling team positioned on the ground
 * - FielderPosition: positionName, x, y coordinates
 */
class FieldPlacementTest {

    // ============================================================
    // Happy path tests
    // ============================================================

    @Test
    fun `should_create_FieldPlacement_with_exactly_eleven_fielder_positions`() {
        val fielders = createElevenFielders()

        val placement = FieldPlacement.create(
            placementId = "placement-1",
            name = "Attacking",
            bowlerType = BowlerType.FAST,
            fielders = fielders
        )

        assertEquals("placement-1", placement.placementId)
        assertEquals("Attacking", placement.name)
        assertEquals(BowlerType.FAST, placement.bowlerType)
        assertEquals(11, placement.fielders.size)
    }

    @Test
    fun `should_create_FieldPlacement_with_valid_fielder_positions`() {
        val fielders = createElevenFielders()

        val placement = FieldPlacement.create(
            placementId = "placement-2",
            name = "Defensive",
            bowlerType = BowlerType.OFF_SPIN,
            fielders = fielders
        )

        placement.fielders.forEach { fielder ->
            assertNotNull("Fielder position name should not be null", fielder.positionName)
            assertTrue("Fielder x coordinate should be valid", fielder.x in -1.0f..1.0f)
            assertTrue("Fielder y coordinate should be valid", fielder.y in -1.0f..1.0f)
        }
    }

    // ============================================================
    // Unhappy path tests
    // ============================================================

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_FieldPlacement_with_fewer_than_eleven_fielders`() {
        val fielders = (1..10).map { i ->
            FielderPosition("Position-$i", 0.1f * i, 0.1f * i)
        }

        FieldPlacement.create(
            placementId = "placement-invalid-1",
            name = "Too Few",
            bowlerType = BowlerType.FAST,
            fielders = fielders
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_FieldPlacement_with_more_than_eleven_fielders`() {
        val fielders = (1..12).map { i ->
            FielderPosition("Position-$i", 0.1f * i, 0.1f * i)
        }

        FieldPlacement.create(
            placementId = "placement-invalid-2",
            name = "Too Many",
            bowlerType = BowlerType.FAST,
            fielders = fielders
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_FieldPlacement_with_empty_fielders_list`() {
        FieldPlacement.create(
            placementId = "placement-invalid-3",
            name = "Empty",
            bowlerType = BowlerType.FAST,
            fielders = emptyList()
        )
    }

    // ============================================================
    // Edge case tests
    // ============================================================

    @Test
    fun `should_create_FieldPlacement_with_exactly_eleven_fielders_boundary`() {
        val fielders = createElevenFielders()

        val placement = FieldPlacement.create(
            placementId = "placement-edge-1",
            name = "Standard",
            bowlerType = BowlerType.LEG_SPIN,
            fielders = fielders
        )

        assertEquals(11, placement.fielders.size)
    }

    @Test
    fun `should_create_FieldPlacement_for_all_bowlerTypes`() {
        val fielders = createElevenFielders()

        val fastPlacement = FieldPlacement.create("p1", "Fast Field", BowlerType.FAST, fielders)
        val mediumPlacement = FieldPlacement.create("p2", "Med Field", BowlerType.MEDIUM_FAST, fielders)
        val offSpinPlacement = FieldPlacement.create("p3", "Off Field", BowlerType.OFF_SPIN, fielders)
        val legSpinPlacement = FieldPlacement.create("p4", "Leg Field", BowlerType.LEG_SPIN, fielders)

        assertEquals(BowlerType.FAST, fastPlacement.bowlerType)
        assertEquals(BowlerType.MEDIUM_FAST, mediumPlacement.bowlerType)
        assertEquals(BowlerType.OFF_SPIN, offSpinPlacement.bowlerType)
        assertEquals(BowlerType.LEG_SPIN, legSpinPlacement.bowlerType)
    }

    // ============================================================
    // Helper methods
    // ============================================================

    private fun createElevenFielders(): List<FielderPosition> {
        return listOf(
            FielderPosition("Bowler", 0.0f, 0.5f),
            FielderPosition("Wicket-Keeper", 0.0f, -0.5f),
            FielderPosition("Slip 1", 0.2f, -0.4f),
            FielderPosition("Slip 2", 0.3f, -0.35f),
            FielderPosition("Cover", 0.5f, 0.0f),
            FielderPosition("Mid-Off", 0.3f, 0.3f),
            FielderPosition("Mid-On", -0.3f, 0.3f),
            FielderPosition("Mid-Wicket", -0.5f, 0.0f),
            FielderPosition("Square Leg", -0.5f, -0.3f),
            FielderPosition("Fine Leg", -0.2f, -0.5f),
            FielderPosition("Third Man", 0.2f, -0.5f)
        )
    }
}
