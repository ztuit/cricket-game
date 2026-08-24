package com.cricketgame.domain.delivery

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for BallCharacteristics value object.
 *
 * Ubiquitous language terms (from ddd.md):
 * - BallCharacteristics: Value object bundling line, length, pace, and spin information for a delivery
 * - Line: Horizontal trajectory of the delivery relative to the stumps
 * - Length: Where the delivery pitches on the surface (Full, Good Length, Short, Yorker)
 * - Pace: Speed of the delivery (float 0-1)
 * - Spin: Rotational movement of the ball (float 0-1, deliberately imprecise)
 *
 * Security requirement (SEC-003): BallCharacteristics rejects pace/spin outside 0-1
 */
class BallCharacteristicsTest {

    // ============================================================
    // Happy path tests
    // ============================================================

    @Test
    fun `should_create_BallCharacteristics_with_valid_line_length_pace_and_spin`() {
        val ball = BallCharacteristics(
            line = Line.OFF_STUMP,
            length = Length.GOOD_LENGTH,
            pace = 0.7f,
            spin = 0.3f
        )

        assertEquals(Line.OFF_STUMP, ball.line)
        assertEquals(Length.GOOD_LENGTH, ball.length)
        assertEquals(0.7f, ball.pace, 0.001f)
        assertEquals(0.3f, ball.spin, 0.001f)
    }

    @Test
    fun `should_create_BallCharacteristics_with_all_valid_Line_values`() {
        val offStump = BallCharacteristics(Line.OFF_STUMP, Length.GOOD_LENGTH, 0.5f, 0.0f)
        val middle = BallCharacteristics(Line.MIDDLE, Length.GOOD_LENGTH, 0.5f, 0.0f)
        val leg = BallCharacteristics(Line.LEG, Length.GOOD_LENGTH, 0.5f, 0.0f)
        val outsideOff = BallCharacteristics(Line.OUTSIDE_OFF, Length.GOOD_LENGTH, 0.5f, 0.0f)
        val outsideLeg = BallCharacteristics(Line.OUTSIDE_LEG, Length.GOOD_LENGTH, 0.5f, 0.0f)

        assertEquals(Line.OFF_STUMP, offStump.line)
        assertEquals(Line.MIDDLE, middle.line)
        assertEquals(Line.LEG, leg.line)
        assertEquals(Line.OUTSIDE_OFF, outsideOff.line)
        assertEquals(Line.OUTSIDE_LEG, outsideLeg.line)
    }

    @Test
    fun `should_create_BallCharacteristics_with_all_valid_Length_values`() {
        val full = BallCharacteristics(Line.OFF_STUMP, Length.FULL, 0.5f, 0.0f)
        val goodLength = BallCharacteristics(Line.OFF_STUMP, Length.GOOD_LENGTH, 0.5f, 0.0f)
        val short_ = BallCharacteristics(Line.OFF_STUMP, Length.SHORT, 0.5f, 0.0f)
        val yorker = BallCharacteristics(Line.OFF_STUMP, Length.YORKER, 0.5f, 0.0f)

        assertEquals(Length.FULL, full.length)
        assertEquals(Length.GOOD_LENGTH, goodLength.length)
        assertEquals(Length.SHORT, short_.length)
        assertEquals(Length.YORKER, yorker.length)
    }

    // ============================================================
    // Unhappy path tests — SEC-003 validation
    // ============================================================

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_BallCharacteristics_with_pace_below_zero`() {
        BallCharacteristics(
            line = Line.OFF_STUMP,
            length = Length.GOOD_LENGTH,
            pace = -0.1f,
            spin = 0.5f
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_BallCharacteristics_with_pace_above_one`() {
        BallCharacteristics(
            line = Line.OFF_STUMP,
            length = Length.GOOD_LENGTH,
            pace = 1.1f,
            spin = 0.5f
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_BallCharacteristics_with_spin_below_zero`() {
        BallCharacteristics(
            line = Line.OFF_STUMP,
            length = Length.GOOD_LENGTH,
            pace = 0.5f,
            spin = -0.1f
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_BallCharacteristics_with_spin_above_one`() {
        BallCharacteristics(
            line = Line.OFF_STUMP,
            length = Length.GOOD_LENGTH,
            pace = 0.5f,
            spin = 1.1f
        )
    }

    // ============================================================
    // Edge case tests — boundary values
    // ============================================================

    @Test
    fun `should_create_BallCharacteristics_with_pace_at_zero_boundary`() {
        val ball = BallCharacteristics(
            line = Line.OFF_STUMP,
            length = Length.GOOD_LENGTH,
            pace = 0.0f,
            spin = 0.5f
        )

        assertEquals(0.0f, ball.pace, 0.001f)
    }

    @Test
    fun `should_create_BallCharacteristics_with_pace_at_one_boundary`() {
        val ball = BallCharacteristics(
            line = Line.OFF_STUMP,
            length = Length.GOOD_LENGTH,
            pace = 1.0f,
            spin = 0.5f
        )

        assertEquals(1.0f, ball.pace, 0.001f)
    }

    @Test
    fun `should_create_BallCharacteristics_with_spin_at_zero_boundary`() {
        val ball = BallCharacteristics(
            line = Line.OFF_STUMP,
            length = Length.GOOD_LENGTH,
            pace = 0.5f,
            spin = 0.0f
        )

        assertEquals(0.0f, ball.spin, 0.001f)
    }

    @Test
    fun `should_create_BallCharacteristics_with_spin_at_one_boundary`() {
        val ball = BallCharacteristics(
            line = Line.OFF_STUMP,
            length = Length.GOOD_LENGTH,
            pace = 0.5f,
            spin = 1.0f
        )

        assertEquals(1.0f, ball.spin, 0.001f)
    }

    @Test
    fun `should_create_BallCharacteristics_with_maximum_values_pace_and_spin_at_one`() {
        val ball = BallCharacteristics(
            line = Line.OUTSIDE_OFF,
            length = Length.YORKER,
            pace = 1.0f,
            spin = 1.0f
        )

        assertEquals(1.0f, ball.pace, 0.001f)
        assertEquals(1.0f, ball.spin, 0.001f)
    }

    @Test
    fun `should_create_BallCharacteristics_with_minimum_values_pace_and_spin_at_zero`() {
        val ball = BallCharacteristics(
            line = Line.OUTSIDE_LEG,
            length = Length.FULL,
            pace = 0.0f,
            spin = 0.0f
        )

        assertEquals(0.0f, ball.pace, 0.001f)
        assertEquals(0.0f, ball.spin, 0.001f)
    }
}
