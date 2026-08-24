package com.cricketgame.domain.delivery

import com.cricketgame.domain.match.DomainEvent
import com.cricketgame.domain.player.BatsmanStats
import com.cricketgame.domain.player.BowlerStats
import com.cricketgame.domain.pitch.SurfaceCondition
import com.cricketgame.domain.pitch.Weather
import com.cricketgame.domain.pitch.WeatherCondition
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for Delivery aggregate creation and domain events.
 *
 * Ubiquitous language terms (from ddd.md):
 * - Delivery: A single ball bowled from the bowler to the batsman. The atomic unit of gameplay.
 * - BallCharacteristics: Value object bundling line, length, pace, spin
 * - ShotSelection: Value object with shotType and wristAngle
 * - Outcome: The result of a delivery
 * - Domain events: DeliveryBowled, ShotPlayed, OutcomeResolved
 *
 * Architecture (ADR-005): Delivery is a pure function — inputs in, Outcome out.
 * The probability model lives in domain.delivery as a pure function.
 */
class DeliveryTest {

    // ============================================================
    // Happy path tests — Delivery aggregate creation
    // ============================================================

    @Test
    fun `should_create_Delivery_with_valid_BallCharacteristics_and_ShotSelection`() {
        val delivery = Delivery.create(
            deliveryId = "delivery-001",
            deliveryNumber = 0,
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection()
        )

        assertEquals("delivery-001", delivery.deliveryId)
        assertEquals(0, delivery.deliveryNumber)
        assertNotNull("BallCharacteristics should be set", delivery.ballCharacteristics)
        assertNotNull("ShotSelection should be set", delivery.shotSelection)
    }

    @Test
    fun `should_create_Delivery_with_all_valid_Line_and_Length_combinations`() {
        // Verify delivery can be created with any valid line/length combination
        for (line in Line.values()) {
            for (length in Length.values()) {
                val delivery = Delivery.create(
                    deliveryId = "delivery-${line.name}-${length.name}",
                    deliveryNumber = 0,
                    ballCharacteristics = BallCharacteristics(line, length, 0.5f, 0.0f),
                    shotSelection = ShotSelection(ShotType.DEFENSIVE, 0.0f)
                )

                assertEquals(line, delivery.ballCharacteristics.line)
                assertEquals(length, delivery.ballCharacteristics.length)
            }
        }
    }

    @Test
    fun `should_create_Delivery_with_all_ten_ShotTypes`() {
        for (shotType in ShotType.values()) {
            val delivery = Delivery.create(
                deliveryId = "delivery-${shotType.name}",
                deliveryNumber = 0,
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = ShotSelection(shotType, 0.0f)
            )

            assertEquals(shotType, delivery.shotSelection.shotType)
        }
    }

    // ============================================================
    // Happy path tests — Domain events
    // ============================================================

    @Test
    fun `should_emit_DeliveryBowled_event_when_delivery_is_created`() {
        val delivery = Delivery.create(
            deliveryId = "delivery-event-1",
            deliveryNumber = 0,
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection()
        )

        val events = delivery.collectEvents()

        assertTrue(
            "DeliveryBowled event should be emitted",
            events.any { it is DomainEvent.DeliveryBowled }
        )

        val deliveryBowled = events.filterIsInstance<DomainEvent.DeliveryBowled>().first()
        assertEquals("delivery-event-1", deliveryBowled.deliveryId)
        assertEquals(createValidBallCharacteristics(), deliveryBowled.ballCharacteristics)
    }

    @Test
    fun `should_emit_ShotPlayed_event_when_delivery_is_created`() {
        val shotSelection = ShotSelection(ShotType.PULL, 45.0f)
        val delivery = Delivery.create(
            deliveryId = "delivery-event-2",
            deliveryNumber = 0,
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = shotSelection
        )

        val events = delivery.collectEvents()

        assertTrue(
            "ShotPlayed event should be emitted",
            events.any { it is DomainEvent.ShotPlayed }
        )

        val shotPlayed = events.filterIsInstance<DomainEvent.ShotPlayed>().first()
        assertEquals(shotSelection, shotPlayed.shotSelection)
    }

    @Test
    fun `should_emit_OutcomeResolved_event_when_outcome_is_resolved`() {
        val delivery = Delivery.create(
            deliveryId = "delivery-event-3",
            deliveryNumber = 0,
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection()
        )

        val outcome = delivery.resolveOutcome(
            bowlerStats = createValidBowlerStats(),
            batsmanStats = createValidBatsmanStats(),
            surfaceCondition = createValidSurfaceCondition(),
            weather = createValidWeather(),
            ballAge = 0,
            seed = 42L
        )

        val events = delivery.collectEvents()

        assertTrue(
            "OutcomeResolved event should be emitted",
            events.any { it is DomainEvent.OutcomeResolved }
        )

        val outcomeResolved = events.filterIsInstance<DomainEvent.OutcomeResolved>().first()
        assertEquals(outcome, outcomeResolved.outcome)
        assertEquals(outcome.runs, outcomeResolved.runsScored)
        assertEquals(outcome.type == OutcomeType.WICKET, outcomeResolved.isWicket)
    }

    @Test
    fun `should_emit_all_three_domain_events_for_a_complete_delivery`() {
        val delivery = Delivery.create(
            deliveryId = "delivery-all-events",
            deliveryNumber = 0,
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection()
        )

        delivery.resolveOutcome(
            bowlerStats = createValidBowlerStats(),
            batsmanStats = createValidBatsmanStats(),
            surfaceCondition = createValidSurfaceCondition(),
            weather = createValidWeather(),
            ballAge = 0,
            seed = 42L
        )

        val events = delivery.collectEvents()

        assertTrue("DeliveryBowled should be emitted", events.any { it is DomainEvent.DeliveryBowled })
        assertTrue("ShotPlayed should be emitted", events.any { it is DomainEvent.ShotPlayed })
        assertTrue("OutcomeResolved should be emitted", events.any { it is DomainEvent.OutcomeResolved })
    }

    // ============================================================
    // Happy path tests — Outcome resolution
    // ============================================================

    @Test
    fun `should_resolve_outcome_for_each_shot_type`() {
        for (shotType in ShotType.values()) {
            val delivery = Delivery.create(
                deliveryId = "delivery-resolve-${shotType.name}",
                deliveryNumber = 0,
                ballCharacteristics = createValidBallCharacteristics(),
                shotSelection = ShotSelection(shotType, 0.0f)
            )

            val outcome = delivery.resolveOutcome(
                bowlerStats = createValidBowlerStats(),
                batsmanStats = createValidBatsmanStats(),
                surfaceCondition = createValidSurfaceCondition(),
                weather = createValidWeather(),
                ballAge = 0,
                seed = 42L
            )

            assertNotNull("Outcome should not be null for $shotType", outcome)
            assertTrue("Runs should be non-negative for $shotType", outcome.runs >= 0)
        }
    }

    @Test
    fun `should_produce_valid_Outcome_type_for_resolution`() {
        val delivery = Delivery.create(
            deliveryId = "delivery-outcome-types",
            deliveryNumber = 0,
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection()
        )

        val outcome = delivery.resolveOutcome(
            bowlerStats = createValidBowlerStats(),
            batsmanStats = createValidBatsmanStats(),
            surfaceCondition = createValidSurfaceCondition(),
            weather = createValidWeather(),
            ballAge = 0,
            seed = 42L
        )

        val validTypes = OutcomeType.values()
        assertTrue(
            "Outcome type should be one of: ${validTypes.joinToString()}",
            validTypes.contains(outcome.type)
        )
    }

    // ============================================================
    // Unhappy path tests
    // ============================================================

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_Delivery_with_negative_deliveryNumber`() {
        Delivery.create(
            deliveryId = "delivery-bad-1",
            deliveryNumber = -1,
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection()
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_Delivery_with_empty_deliveryId`() {
        Delivery.create(
            deliveryId = "",
            deliveryNumber = 0,
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection()
        )
    }

    // ============================================================
    // Edge case tests
    // ============================================================

    @Test
    fun `should_create_Delivery_with_deliveryNumber_at_zero_boundary`() {
        val delivery = Delivery.create(
            deliveryId = "delivery-edge-1",
            deliveryNumber = 0,
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection()
        )

        assertEquals(0, delivery.deliveryNumber)
    }

    @Test
    fun `should_create_Delivery_with_deliveryNumber_at_five_boundary`() {
        // Max 6 legal deliveries per over (0-5)
        val delivery = Delivery.create(
            deliveryId = "delivery-edge-2",
            deliveryNumber = 5,
            ballCharacteristics = createValidBallCharacteristics(),
            shotSelection = createValidShotSelection()
        )

        assertEquals(5, delivery.deliveryNumber)
    }

    @Test
    fun `should_create_Delivery_with_maximum_ballCharacteristics_values`() {
        val delivery = Delivery.create(
            deliveryId = "delivery-edge-3",
            deliveryNumber = 0,
            ballCharacteristics = BallCharacteristics(Line.OUTSIDE_OFF, Length.YORKER, 1.0f, 1.0f),
            shotSelection = createValidShotSelection()
        )

        assertEquals(1.0f, delivery.ballCharacteristics.pace, 0.001f)
        assertEquals(1.0f, delivery.ballCharacteristics.spin, 0.001f)
    }

    @Test
    fun `should_create_Delivery_with_minimum_ballCharacteristics_values`() {
        val delivery = Delivery.create(
            deliveryId = "delivery-edge-4",
            deliveryNumber = 0,
            ballCharacteristics = BallCharacteristics(Line.OUTSIDE_LEG, Length.FULL, 0.0f, 0.0f),
            shotSelection = createValidShotSelection()
        )

        assertEquals(0.0f, delivery.ballCharacteristics.pace, 0.001f)
        assertEquals(0.0f, delivery.ballCharacteristics.spin, 0.001f)
    }

    // ============================================================
    // Helper methods
    // ============================================================

    private fun createValidBallCharacteristics(): BallCharacteristics {
        return BallCharacteristics(
            line = Line.OFF_STUMP,
            length = Length.GOOD_LENGTH,
            pace = 0.7f,
            spin = 0.3f
        )
    }

    private fun createValidShotSelection(): ShotSelection {
        return ShotSelection(
            shotType = ShotType.DRIVE,
            wristAngle = 45.0f
        )
    }

    private fun createValidBowlerStats(): BowlerStats {
        return BowlerStats(
            bowlingSkill = 0.8f,
            accuracy = 0.7f,
            variation = 0.6f,
            wideRate = 0.05f,
            noBallRate = 0.03f
        )
    }

    private fun createValidBatsmanStats(): BatsmanStats {
        return BatsmanStats(
            battingSkill = 0.75f,
            timing = 0.7f,
            power = 0.65f,
            composure = 0.8f
        )
    }

    private fun createValidSurfaceCondition(): SurfaceCondition {
        return SurfaceCondition(
            zoneId = "zone-1",
            degradation = 0.2f,
            moisture = 0.5f,
            roughness = 0.1f
        )
    }

    private fun createValidWeather(): Weather {
        return Weather(
            condition = WeatherCondition.SUNNY,
            temperature = 25.0f,
            humidity = 0.4f
        )
    }
}
