package com.cricketgame.domain.delivery

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for Outcome value object and DismissalType enum.
 *
 * Ubiquitous language terms (from ddd.md):
 * - Outcome: The result of a delivery — runs scored, dismissal type, wide, no ball
 * - Dismissal: How the batsman gets out (Bowled, Caught, LBW, Stumped, RunOut)
 * - Runs: Unit of scoring (0-6 for normal deliveries)
 * - Wide: Illegal delivery too far from batsman (1 run, re-bowled)
 * - NoBall: Illegal delivery e.g. bowler oversteps (1 run, re-bowled)
 * - DotBall: No runs scored from the delivery
 *
 * Invariants (from ddd.md):
 * - If type=Wicket, dismissalType must be set
 * - Runs >= 0
 * - RunOut excluded for single batsman model (ADR-007)
 */
class OutcomeTest {

    // ============================================================
    // Happy path tests — Outcome types
    // ============================================================

    @Test
    fun `should_create_Runs_outcome_with_valid_run_count`() {
        val outcome = Outcome(
            type = OutcomeType.RUNS,
            runs = 4,
            dismissalType = null
        )

        assertEquals(OutcomeType.RUNS, outcome.type)
        assertEquals(4, outcome.runs)
        assertNull("Runs outcome should not have dismissalType", outcome.dismissalType)
    }

    @Test
    fun `should_create_Runs_outcome_for_all_valid_run_values_zero_to_six`() {
        // Runs can be 0-6 for normal deliveries
        for (runs in 0..6) {
            val outcome = Outcome(OutcomeType.RUNS, runs, null)
            assertEquals("Runs should be $runs", runs, outcome.runs)
        }
    }

    @Test
    fun `should_create_Wicket_outcome_with_Bowled_dismissal`() {
        val outcome = Outcome(
            type = OutcomeType.WICKET,
            runs = 0,
            dismissalType = DismissalType.BOWLED
        )

        assertEquals(OutcomeType.WICKET, outcome.type)
        assertEquals(0, outcome.runs)
        assertEquals(DismissalType.BOWLED, outcome.dismissalType)
    }

    @Test
    fun `should_create_Wicket_outcome_with_Caught_dismissal`() {
        val outcome = Outcome(
            type = OutcomeType.WICKET,
            runs = 0,
            dismissalType = DismissalType.CAUGHT
        )

        assertEquals(DismissalType.CAUGHT, outcome.dismissalType)
    }

    @Test
    fun `should_create_Wicket_outcome_with_LBW_dismissal`() {
        val outcome = Outcome(
            type = OutcomeType.WICKET,
            runs = 0,
            dismissalType = DismissalType.LBW
        )

        assertEquals(DismissalType.LBW, outcome.dismissalType)
    }

    @Test
    fun `should_create_Wicket_outcome_with_Stumped_dismissal`() {
        val outcome = Outcome(
            type = OutcomeType.WICKET,
            runs = 0,
            dismissalType = DismissalType.STUMPED
        )

        assertEquals(DismissalType.STUMPED, outcome.dismissalType)
    }

    @Test
    fun `should_create_Wide_outcome_with_one_run`() {
        val outcome = Outcome(
            type = OutcomeType.WIDE,
            runs = 1,
            dismissalType = null
        )

        assertEquals(OutcomeType.WIDE, outcome.type)
        assertEquals(1, outcome.runs)
        assertNull("Wide should not have dismissalType", outcome.dismissalType)
    }

    @Test
    fun `should_create_NoBall_outcome_with_one_run`() {
        val outcome = Outcome(
            type = OutcomeType.NO_BALL,
            runs = 1,
            dismissalType = null
        )

        assertEquals(OutcomeType.NO_BALL, outcome.type)
        assertEquals(1, outcome.runs)
        assertNull("NoBall should not have dismissalType", outcome.dismissalType)
    }

    @Test
    fun `should_create_DotBall_outcome_with_zero_runs`() {
        val outcome = Outcome(
            type = OutcomeType.DOT_BALL,
            runs = 0,
            dismissalType = null
        )

        assertEquals(OutcomeType.DOT_BALL, outcome.type)
        assertEquals(0, outcome.runs)
        assertNull("DotBall should not have dismissalType", outcome.dismissalType)
    }

    // ============================================================
    // Happy path tests — DismissalType enum
    // ============================================================

    @Test
    fun `should_have_four_dismissal_types_for_single_batsman_model`() {
        // RunOut excluded for single batsman model (ADR-007)
        val allTypes = DismissalType.values()

        assertEquals("There should be 4 dismissal types for single batsman model", 4, allTypes.size)

        assertTrue("BOWLED should be available", allTypes.contains(DismissalType.BOWLED))
        assertTrue("CAUGHT should be available", allTypes.contains(DismissalType.CAUGHT))
        assertTrue("LBW should be available", allTypes.contains(DismissalType.LBW))
        assertTrue("STUMPED should be available", allTypes.contains(DismissalType.STUMPED))
    }

    @Test
    fun `should_have_five_outcome_types`() {
        val allTypes = OutcomeType.values()

        assertEquals("There should be 5 outcome types", 5, allTypes.size)

        assertTrue("RUNS should be available", allTypes.contains(OutcomeType.RUNS))
        assertTrue("WICKET should be available", allTypes.contains(OutcomeType.WICKET))
        assertTrue("WIDE should be available", allTypes.contains(OutcomeType.WIDE))
        assertTrue("NO_BALL should be available", allTypes.contains(OutcomeType.NO_BALL))
        assertTrue("DOT_BALL should be available", allTypes.contains(OutcomeType.DOT_BALL))
    }

    // ============================================================
    // Unhappy path tests — invariant violations
    // ============================================================

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_Wicket_outcome_without_dismissalType`() {
        Outcome(
            type = OutcomeType.WICKET,
            runs = 0,
            dismissalType = null
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_Outcome_with_negative_runs`() {
        Outcome(
            type = OutcomeType.RUNS,
            runs = -1,
            dismissalType = null
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_Runs_outcome_with_dismissalType_set`() {
        Outcome(
            type = OutcomeType.RUNS,
            runs = 4,
            dismissalType = DismissalType.BOWLED
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_Wide_outcome_with_dismissalType_set`() {
        Outcome(
            type = OutcomeType.WIDE,
            runs = 1,
            dismissalType = DismissalType.CAUGHT
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_NoBall_outcome_with_dismissalType_set`() {
        Outcome(
            type = OutcomeType.NO_BALL,
            runs = 1,
            dismissalType = DismissalType.LBW
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_DotBall_outcome_with_dismissalType_set`() {
        Outcome(
            type = OutcomeType.DOT_BALL,
            runs = 0,
            dismissalType = DismissalType.STUMPED
        )
    }

    // ============================================================
    // Edge case tests
    // ============================================================

    @Test
    fun `should_create_Runs_outcome_with_exactly_zero_runs_as_dot_ball`() {
        // Dot ball is a specific outcome type, but Runs with 0 is also valid
        val runsOutcome = Outcome(OutcomeType.RUNS, 0, null)

        assertEquals(OutcomeType.RUNS, runsOutcome.type)
        assertEquals(0, runsOutcome.runs)
    }

    @Test
    fun `should_create_Runs_outcome_with_exactly_six_runs_boundary`() {
        val outcome = Outcome(OutcomeType.RUNS, 6, null)

        assertEquals(6, outcome.runs)
    }

    @Test
    fun `should_create_Wicket_outcome_with_all_four_dismissal_types`() {
        val bowled = Outcome(OutcomeType.WICKET, 0, DismissalType.BOWLED)
        val caught = Outcome(OutcomeType.WICKET, 0, DismissalType.CAUGHT)
        val lbw = Outcome(OutcomeType.WICKET, 0, DismissalType.LBW)
        val stumped = Outcome(OutcomeType.WICKET, 0, DismissalType.STUMPED)

        assertEquals(DismissalType.BOWLED, bowled.dismissalType)
        assertEquals(DismissalType.CAUGHT, caught.dismissalType)
        assertEquals(DismissalType.LBW, lbw.dismissalType)
        assertEquals(DismissalType.STUMPED, stumped.dismissalType)
    }

    @Test
    fun `should_create_Wide_with_exactly_one_run_and_is_rebowled`() {
        // Wide awards 1 run and ball is re-bowled (isLegal = false)
        val outcome = Outcome(OutcomeType.WIDE, 1, null)

        assertEquals(1, outcome.runs)
        assertFalse("Wide delivery should not be legal", outcome.isLegal)
    }

    @Test
    fun `should_create_NoBall_with_exactly_one_run_and_is_rebowled`() {
        // NoBall awards 1 run and ball is re-bowled (isLegal = false)
        val outcome = Outcome(OutcomeType.NO_BALL, 1, null)

        assertEquals(1, outcome.runs)
        assertFalse("NoBall delivery should not be legal", outcome.isLegal)
    }

    @Test
    fun `should_mark_legal_deliveries_as_legal`() {
        val runs = Outcome(OutcomeType.RUNS, 4, null)
        val wicket = Outcome(OutcomeType.WICKET, 0, DismissalType.BOWLED)
        val dotBall = Outcome(OutcomeType.DOT_BALL, 0, null)

        assertTrue("Runs delivery should be legal", runs.isLegal)
        assertTrue("Wicket delivery should be legal", wicket.isLegal)
        assertTrue("DotBall delivery should be legal", dotBall.isLegal)
    }
}
