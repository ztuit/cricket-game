package com.cricketgame.domain.match

import com.cricketgame.domain.delivery.DismissalType
import com.cricketgame.domain.delivery.Outcome
import com.cricketgame.domain.delivery.OutcomeType
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for scoring and match state tracking (INCR-003).
 *
 * Ubiquitous language terms (from ddd.md):
 * - Match: A single T20 game (20 overs max, 10 wickets)
 * - InningsProgress: Current state — overs completed, wickets fallen, score
 * - Delivery: A single ball bowled (the atomic unit of gameplay)
 * - Outcome: Result of a delivery — runs scored, dismissal type, wide, no ball
 * - Over: 6 legal deliveries
 * - Boundary: Ball reaching the edge — 4 runs (along ground), 6 runs (in air)
 * - Wide: Illegal delivery, 1 run, ball re-bowled (not legal)
 * - No Ball: Illegal delivery, 1 run, ball re-bowled (not legal)
 * - Target: Score the player must reach to win
 * - Domain events: BoundaryScored, WicketFallen, OverCompleted, InningsCompleted,
 *   MatchCompleted, TargetReached
 */
class ScoringTest {

    // ============================================================
    // Helper: create a standard chasing match
    // ============================================================

    private fun createChasingMatch(target: Int = 165): Match {
        return Match.create(
            matchId = "match-scoring",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5"),
            target = target
        )
    }

    private fun createBattingFirstMatch(): Match {
        return Match.create(
            matchId = "match-scoring",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5"),
            target = null
        )
    }

    private fun runsOutcome(runs: Int): Outcome = Outcome(
        type = OutcomeType.RUNS,
        runs = runs,
        dismissalType = null
    )

    private fun wicketOutcome(dismissalType: DismissalType = DismissalType.BOWLED): Outcome = Outcome(
        type = OutcomeType.WICKET,
        runs = 0,
        dismissalType = dismissalType
    )

    private fun wideOutcome(): Outcome = Outcome(
        type = OutcomeType.WIDE,
        runs = 1,
        dismissalType = null
    )

    private fun noBallOutcome(): Outcome = Outcome(
        type = OutcomeType.NO_BALL,
        runs = 1,
        dismissalType = null
    )

    private fun dotBallOutcome(): Outcome = Outcome(
        type = OutcomeType.DOT_BALL,
        runs = 0,
        dismissalType = null
    )

    // ============================================================
    // Happy path tests — InningsProgress updates
    // ============================================================

    @Test
    fun `should_update_InningsProgress_runs_after_delivery_with_runs`() {
        val match = createChasingMatch()

        match.processOutcome(runsOutcome(4))

        val progress = match.inningsProgress
        assertEquals("currentScore should be 4 after scoring 4 runs", 4, progress.currentScore)
    }

    @Test
    fun `should_increment_wicketsFallen_after_wicket_delivery`() {
        val match = createChasingMatch()

        match.processOutcome(wicketOutcome())

        val progress = match.inningsProgress
        assertEquals("wicketsFallen should be 1 after a wicket", 1, progress.wicketsFallen)
    }

    @Test
    fun `should_increment_ballsThisOver_after_legal_delivery`() {
        val match = createChasingMatch()

        match.processOutcome(dotBallOutcome())

        val progress = match.inningsProgress
        assertEquals("ballsThisOver should be 1 after a legal delivery", 1, progress.ballsThisOver)
    }

    @Test
    fun `should_not_increment_ballsThisOver_after_wide`() {
        val match = createChasingMatch()

        match.processOutcome(wideOutcome())

        val progress = match.inningsProgress
        assertEquals("ballsThisOver should remain 0 after a wide (not legal)", 0, progress.ballsThisOver)
    }

    @Test
    fun `should_not_increment_ballsThisOver_after_no_ball`() {
        val match = createChasingMatch()

        match.processOutcome(noBallOutcome())

        val progress = match.inningsProgress
        assertEquals("ballsThisOver should remain 0 after a no ball (not legal)", 0, progress.ballsThisOver)
    }

    // ============================================================
    // Happy path tests — Boundary detection
    // ============================================================

    @Test
    fun `should_emit_BoundaryScored_event_when_runs_is_four`() {
        val match = createChasingMatch()

        match.processOutcome(runsOutcome(4))

        val events = match.collectEvents()
        val boundaryEvents = events.filterIsInstance<DomainEvent.BoundaryScored>()
        assertFalse("BoundaryScored event should be emitted for 4 runs", boundaryEvents.isEmpty())
        assertEquals("BoundaryScored runs should be 4", 4, boundaryEvents.first().runs)
    }

    @Test
    fun `should_emit_BoundaryScored_event_when_runs_is_six`() {
        val match = createChasingMatch()

        match.processOutcome(runsOutcome(6))

        val events = match.collectEvents()
        val boundaryEvents = events.filterIsInstance<DomainEvent.BoundaryScored>()
        assertFalse("BoundaryScored event should be emitted for 6 runs", boundaryEvents.isEmpty())
        assertEquals("BoundaryScored runs should be 6", 6, boundaryEvents.first().runs)
    }

    // ============================================================
    // Happy path tests — Wide and NoBall
    // ============================================================

    @Test
    fun `should_add_one_run_for_wide_and_not_count_as_legal_delivery`() {
        val match = createChasingMatch()

        match.processOutcome(wideOutcome())

        val progress = match.inningsProgress
        assertEquals("currentScore should increase by 1 for wide", 1, progress.currentScore)
        assertEquals("ballsThisOver should not increment for wide (not legal)", 0, progress.ballsThisOver)
    }

    @Test
    fun `should_add_one_run_for_no_ball_and_not_count_as_legal_delivery`() {
        val match = createChasingMatch()

        match.processOutcome(noBallOutcome())

        val progress = match.inningsProgress
        assertEquals("currentScore should increase by 1 for no ball", 1, progress.currentScore)
        assertEquals("ballsThisOver should not increment for no ball (not legal)", 0, progress.ballsThisOver)
    }

    // ============================================================
    // Happy path tests — Over completion
    // ============================================================

    @Test
    fun `should_complete_over_after_six_legal_deliveries`() {
        val match = createChasingMatch()

        // Bowl 6 legal deliveries (dot balls)
        repeat(6) { match.processOutcome(dotBallOutcome()) }

        val progress = match.inningsProgress
        assertEquals("oversCompleted should be 1 after 6 legal deliveries", 1, progress.oversCompleted)
        assertEquals("ballsThisOver should reset to 0 after over completes", 0, progress.ballsThisOver)
    }

    @Test
    fun `should_emit_OverCompleted_event_when_over_finishes`() {
        val match = createChasingMatch()

        repeat(6) { match.processOutcome(dotBallOutcome()) }

        val events = match.collectEvents()
        val overCompletedEvents = events.filterIsInstance<DomainEvent.OverCompleted>()
        assertFalse("OverCompleted event should be emitted", overCompletedEvents.isEmpty())
        assertEquals("OverCompleted overNumber should be 0 (first over)", 0, overCompletedEvents.first().overNumber)
    }

    // ============================================================
    // Happy path tests — Innings end conditions
    // ============================================================

    @Test
    fun `should_end_innings_when_twenty_overs_bowled`() {
        val match = createBattingFirstMatch()

        // Bowl 20 overs (120 legal deliveries)
        repeat(120) { match.processOutcome(dotBallOutcome()) }

        val events = match.collectEvents()
        val inningsCompletedEvents = events.filterIsInstance<DomainEvent.InningsCompleted>()
        assertFalse("InningsCompleted event should be emitted after 20 overs", inningsCompletedEvents.isEmpty())
    }

    @Test
    fun `should_end_innings_when_ten_wickets_fallen`() {
        val match = createBattingFirstMatch()

        // Lose 10 wickets
        repeat(10) { match.processOutcome(wicketOutcome()) }

        val events = match.collectEvents()
        val inningsCompletedEvents = events.filterIsInstance<DomainEvent.InningsCompleted>()
        assertFalse("InningsCompleted event should be emitted after 10 wickets", inningsCompletedEvents.isEmpty())
    }

    @Test
    fun `should_end_innings_when_target_reached`() {
        val match = createChasingMatch(target = 10)

        // Score 11 runs to exceed target of 10
        match.processOutcome(runsOutcome(6))
        match.processOutcome(runsOutcome(4))
        match.processOutcome(runsOutcome(1))

        val events = match.collectEvents()
        val targetReachedEvents = events.filterIsInstance<DomainEvent.TargetReached>()
        assertFalse("TargetReached event should be emitted when target exceeded", targetReachedEvents.isEmpty())
        assertEquals("TargetReached finalScore should be 11", 11, targetReachedEvents.first().finalScore)
        assertEquals("TargetReached target should be 10", 10, targetReachedEvents.first().target)
    }

    // ============================================================
    // Happy path tests — Match result
    // ============================================================

    @Test
    fun `should_determine_Win_when_target_exceeded`() {
        val match = createChasingMatch(target = 10)

        match.processOutcome(runsOutcome(6))
        match.processOutcome(runsOutcome(6))

        val events = match.collectEvents()
        val matchCompletedEvents = events.filterIsInstance<DomainEvent.MatchCompleted>()
        assertFalse("MatchCompleted event should be emitted", matchCompletedEvents.isEmpty())
        assertEquals("Match result should be Win", MatchResult.WIN, matchCompletedEvents.first().result)
    }

    @Test
    fun `should_determine_Loss_when_innings_ends_short_of_target`() {
        val match = createChasingMatch(target = 200)

        // Score 4 runs then lose all 10 wickets — well short of target 200
        match.processOutcome(runsOutcome(4))
        repeat(9) { match.processOutcome(wicketOutcome()) }
        // Last wicket
        match.processOutcome(wicketOutcome())

        val events = match.collectEvents()
        val matchCompletedEvents = events.filterIsInstance<DomainEvent.MatchCompleted>()
        assertFalse("MatchCompleted event should be emitted", matchCompletedEvents.isEmpty())
        assertEquals("Match result should be Loss", MatchResult.LOSS, matchCompletedEvents.first().result)
    }

    @Test
    fun `should_determine_Draw_when_scores_equal_after_twenty_overs`() {
        val match = createChasingMatch(target = 5)

        // Score exactly 5 runs (equal to target) then bowl out remaining overs
        match.processOutcome(runsOutcome(4))  // ball 1, score 4
        match.processOutcome(runsOutcome(1))  // ball 2, score 5 (= target, not exceeded)
        // Now score equals target but not exceeded — continue batting
        // Need 118 more legal deliveries to complete 20 overs (120 total)
        repeat(118) { match.processOutcome(dotBallOutcome()) }

        val events = match.collectEvents()
        val matchCompletedEvents = events.filterIsInstance<DomainEvent.MatchCompleted>()
        assertFalse("MatchCompleted event should be emitted", matchCompletedEvents.isEmpty())
        assertEquals("Match result should be Draw when scores equal", MatchResult.DRAW, matchCompletedEvents.first().result)
    }

    // ============================================================
    // Happy path tests — Domain events
    // ============================================================

    @Test
    fun `should_emit_WicketFallen_event_when_wicket_falls`() {
        val match = createChasingMatch()

        match.processOutcome(wicketOutcome(DismissalType.CAUGHT))

        val events = match.collectEvents()
        val wicketEvents = events.filterIsInstance<DomainEvent.WicketFallen>()
        assertFalse("WicketFallen event should be emitted", wicketEvents.isEmpty())
        assertEquals("WicketFallen wicketNumber should be 1", 1, wicketEvents.first().wicketNumber)
        assertEquals("WicketFallen dismissalType should be CAUGHT", DismissalType.CAUGHT, wicketEvents.first().dismissalType)
    }

    @Test
    fun `should_emit_InningsCompleted_event_when_innings_ends`() {
        val match = createBattingFirstMatch()

        // End innings via 10 wickets
        repeat(10) { match.processOutcome(wicketOutcome()) }

        val events = match.collectEvents()
        val inningsCompletedEvents = events.filterIsInstance<DomainEvent.InningsCompleted>()
        assertFalse("InningsCompleted event should be emitted", inningsCompletedEvents.isEmpty())
        assertEquals("InningsCompleted wicketsFallen should be 10", 10, inningsCompletedEvents.first().wicketsFallen)
    }

    @Test
    fun `should_emit_MatchCompleted_event_when_match_ends`() {
        val match = createChasingMatch(target = 5)

        // Win the match
        match.processOutcome(runsOutcome(6))

        val events = match.collectEvents()
        val matchCompletedEvents = events.filterIsInstance<DomainEvent.MatchCompleted>()
        assertFalse("MatchCompleted event should be emitted", matchCompletedEvents.isEmpty())
        assertNotNull("MatchCompleted result should be set", matchCompletedEvents.first().result)
    }

    // ============================================================
    // Unhappy path tests
    // ============================================================

    @Test(expected = IllegalStateException::class)
    fun `should_reject_processOutcome_when_match_already_complete`() {
        val match = createChasingMatch(target = 5)

        // Complete the match
        match.processOutcome(runsOutcome(6))

        // This should throw — match is already complete
        match.processOutcome(dotBallOutcome())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_InningsProgress_with_wickets_exceeding_ten_after_update`() {
        // InningsProgress invariants must still hold after any update
        InningsProgress(
            oversCompleted = 0,
            ballsThisOver = 0,
            wicketsFallen = 11,
            currentScore = 0,
            target = null
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_InningsProgress_with_balls_exceeding_six_after_update`() {
        // InningsProgress invariants must still hold after any update
        InningsProgress(
            oversCompleted = 0,
            ballsThisOver = 7,
            wicketsFallen = 0,
            currentScore = 0,
            target = null
        )
    }

    // ============================================================
    // Edge case tests
    // ============================================================

    @Test
    fun `should_emit_both_OverCompleted_and_WicketFallen_when_wicket_on_ball_six`() {
        val match = createChasingMatch()

        // Bowl 5 dot balls, then a wicket on ball 6
        repeat(5) { match.processOutcome(dotBallOutcome()) }
        match.processOutcome(wicketOutcome(DismissalType.LBW))

        val events = match.collectEvents()
        val overCompletedEvents = events.filterIsInstance<DomainEvent.OverCompleted>()
        val wicketEvents = events.filterIsInstance<DomainEvent.WicketFallen>()

        assertFalse("OverCompleted should be emitted when wicket falls on ball 6", overCompletedEvents.isEmpty())
        assertFalse("WicketFallen should be emitted when wicket falls on ball 6", wicketEvents.isEmpty())
        assertEquals("Over should complete (ballsThisOver resets)", 0, match.inningsProgress.ballsThisOver)
        assertEquals("Wicket should be counted", 1, match.inningsProgress.wicketsFallen)
    }

    @Test
    fun `should_emit_both_BoundaryScored_and_TargetReached_when_boundary_exceeds_target`() {
        val match = createChasingMatch(target = 100)

        // Score 98 runs via boundaries, then hit a 4 to reach 102 (exceeds target of 100)
        // 4 + 15*6 + 4 = 4 + 90 + 4 = 98
        match.processOutcome(runsOutcome(4))
        repeat(15) { match.processOutcome(runsOutcome(6)) }
        match.processOutcome(runsOutcome(4))
        // Score is now 98. Next delivery: 4 runs → score 102, exceeds target 100
        match.processOutcome(runsOutcome(4))

        val events = match.collectEvents()
        val boundaryEvents = events.filterIsInstance<DomainEvent.BoundaryScored>()
        val targetReachedEvents = events.filterIsInstance<DomainEvent.TargetReached>()

        assertTrue("BoundaryScored should be emitted for the final 4-run boundary", boundaryEvents.isNotEmpty())
        assertFalse("TargetReached should be emitted when boundary exceeds target", targetReachedEvents.isEmpty())
        assertEquals("Final score should be 102", 102, targetReachedEvents.first().finalScore)
    }

    @Test
    fun `should_not_complete_over_when_wide_on_ball_six`() {
        val match = createChasingMatch()

        // Bowl 5 legal deliveries, then a wide on what would be ball 6
        repeat(5) { match.processOutcome(dotBallOutcome()) }
        match.processOutcome(wideOutcome())

        val progress = match.inningsProgress
        assertEquals("Over should NOT complete — wide is not a legal delivery", 0, progress.oversCompleted)
        assertEquals("ballsThisOver should remain 5 (wide does not count)", 5, progress.ballsThisOver)
        assertEquals("currentScore should include the wide run", 1, progress.currentScore)
    }

    @Test
    fun `should_determine_Draw_when_score_exactly_equals_target_after_twenty_overs`() {
        val match = createChasingMatch(target = 120)

        // Score exactly 120 runs (equal to target) over 20 overs
        // 20 overs * 6 balls = 120 balls, 1 run per ball = 120 runs
        repeat(120) { match.processOutcome(runsOutcome(1)) }

        val events = match.collectEvents()
        val matchCompletedEvents = events.filterIsInstance<DomainEvent.MatchCompleted>()
        assertFalse("MatchCompleted event should be emitted", matchCompletedEvents.isEmpty())
        assertEquals(
            "Match result should be Draw when score equals target exactly",
            MatchResult.DRAW,
            matchCompletedEvents.first().result
        )
    }

    @Test
    fun `should_track_wickets_across_overs`() {
        val match = createChasingMatch()

        // Over 1: 3 wickets
        match.processOutcome(wicketOutcome())
        match.processOutcome(dotBallOutcome())
        match.processOutcome(wicketOutcome())
        match.processOutcome(dotBallOutcome())
        match.processOutcome(wicketOutcome())
        match.processOutcome(dotBallOutcome())

        assertEquals("Should have 3 wickets after over 1", 3, match.inningsProgress.wicketsFallen)
        assertEquals("Should have completed 1 over", 1, match.inningsProgress.oversCompleted)

        // Over 2: 2 more wickets
        match.processOutcome(wicketOutcome())
        match.processOutcome(dotBallOutcome())
        match.processOutcome(dotBallOutcome())
        match.processOutcome(dotBallOutcome())
        match.processOutcome(dotBallOutcome())
        match.processOutcome(wicketOutcome())

        assertEquals("Should have 5 wickets after over 2", 5, match.inningsProgress.wicketsFallen)
        assertEquals("Should have completed 2 overs", 2, match.inningsProgress.oversCompleted)
    }

    @Test
    fun `should_track_runs_across_mixed_deliveries`() {
        val match = createChasingMatch()

        // Mix of runs, wides, no balls, dot balls
        match.processOutcome(runsOutcome(4))      // 4 runs, ball 1
        match.processOutcome(wideOutcome())        // +1 run, ball stays at 1 (not legal)
        match.processOutcome(runsOutcome(6))       // +6 runs, ball 2
        match.processOutcome(noBallOutcome())      // +1 run, ball stays at 2 (not legal)
        match.processOutcome(dotBallOutcome())     // 0 runs, ball 3
        match.processOutcome(runsOutcome(2))       // +2 runs, ball 4

        assertEquals("currentScore should be 14 (4+1+6+1+0+2)", 14, match.inningsProgress.currentScore)
        assertEquals("ballsThisOver should be 4 (only legal deliveries count)", 4, match.inningsProgress.ballsThisOver)
    }
}
