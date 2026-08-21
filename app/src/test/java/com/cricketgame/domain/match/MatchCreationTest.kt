package com.cricketgame.domain.match

import com.cricketgame.domain.player.BowlerType
import com.cricketgame.domain.player.ExperienceClass
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for Match aggregate creation and toss mechanic.
 *
 * Ubiquitous language terms (from ddd.md):
 * - Match: A single T20 game (20 overs max, 10 wickets)
 * - Toss: Coin toss at match start, determines batting order
 * - TossResult: Winner (Player or AI) and decision (Bat or Field)
 * - Target: Score to chase; null if batting first, positive integer if chasing
 * - InningsProgress: Current state — overs completed, wickets fallen, score
 * - Domain events: MatchStarted, TossCompleted
 */
class MatchCreationTest {

    // ============================================================
    // Happy path tests
    // ============================================================

    @Test
    fun `should_create_match_with_valid_invariants`() {
        // A Match with standard T20 invariants (20 overs, 10 wickets)
        val match = Match.create(
            matchId = "match-001",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )

        assertEquals("match-001", match.matchId)
        assertEquals(20, match.maxOvers)
        assertEquals(10, match.maxWickets)
        assertEquals("ground-001", match.groundId)
        assertEquals(5, match.bowlerRosterIds.size)
    }

    @Test
    fun `should_create_match_with_target_null_when_batting_first`() {
        // When the player bats first, they are setting a target — target is null
        val match = Match.create(
            matchId = "match-002",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5"),
            target = null // batting first — setting a target
        )

        assertNull("Target should be null when batting first", match.target)
    }

    @Test
    fun `should_create_match_with_positive_target_when_chasing`() {
        // When the player chases, target is a pre-calculated positive integer (ADR-008)
        val match = Match.create(
            matchId = "match-003",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5"),
            target = 165
        )

        assertNotNull("Target should be set when chasing", match.target)
        assertTrue("Target should be a positive integer", match.target!! > 0)
        assertEquals(165, match.target)
    }

    @Test
    fun `should_emit_MatchStarted_event_when_match_is_created`() {
        val match = Match.create(
            matchId = "match-004",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )

        val events = match.collectEvents()

        assertTrue(
            "MatchStarted event should be emitted",
            events.any { it is DomainEvent.MatchStarted }
        )

        val matchStarted = events.filterIsInstance<DomainEvent.MatchStarted>().first()
        assertEquals("match-004", matchStarted.matchId)
        assertEquals("ground-001", matchStarted.groundId)
    }

    // ============================================================
    // Toss mechanic tests
    // ============================================================

    @Test
    fun `should_produce_valid_TossResult_when_toss_is_performed`() {
        val match = Match.create(
            matchId = "match-005",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )

        val tossResult = match.performToss()

        assertNotNull("TossResult should not be null", tossResult)
        assertTrue(
            "TossResult winner must be PLAYER or AI",
            tossResult.winner == TossResult.Winner.PLAYER || tossResult.winner == TossResult.Winner.AI
        )
        assertNotNull("TossResult decision should be set", tossResult.decision)
    }

    @Test
    fun `should_allow_player_to_choose_Bat_or_Field_when_player_wins_toss`() {
        val match = Match.create(
            matchId = "match-006",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )

        // Test helper: force player to win the toss
        val tossResult = match.performTossForTest(winner = TossResult.Winner.PLAYER)

        assertEquals(TossResult.Winner.PLAYER, tossResult.winner)
        assertTrue(
            "Player decision should be BAT or FIELD",
            tossResult.decision == TossResult.Decision.BAT || tossResult.decision == TossResult.Decision.FIELD
        )
    }

    @Test
    fun `should_have_AI_choose_when_player_loses_toss`() {
        // Human confirmed: if player loses toss, AI chooses. Player does NOT choose.
        val match = Match.create(
            matchId = "match-007",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )

        val tossResult = match.performTossForTest(winner = TossResult.Winner.AI)

        assertEquals(TossResult.Winner.AI, tossResult.winner)
        assertNotNull("AI decision should be set", tossResult.decision)
    }

    @Test
    fun `should_emit_TossCompleted_event_when_toss_is_performed`() {
        val match = Match.create(
            matchId = "match-008",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )

        match.performToss()
        val events = match.collectEvents()

        assertTrue(
            "TossCompleted event should be emitted",
            events.any { it is DomainEvent.TossCompleted }
        )

        val tossCompleted = events.filterIsInstance<DomainEvent.TossCompleted>().first()
        assertNotNull("TossCompleted should contain tossResult", tossCompleted.tossResult)
    }

    // ============================================================
    // InningsProgress tests
    // ============================================================

    @Test
    fun `should_initialize_InningsProgress_at_zero_overs_zero_wickets_zero_runs`() {
        val progress = InningsProgress.initial(target = null)

        assertEquals(0, progress.oversCompleted)
        assertEquals(0, progress.wicketsFallen)
        assertEquals(0, progress.currentScore)
        assertEquals(0, progress.ballsThisOver)
    }

    // ============================================================
    // Unhappy path tests
    // ============================================================

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_match_creation_with_negative_overs`() {
        Match.create(
            matchId = "match-invalid-1",
            groundId = "ground-001",
            maxOvers = -1,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_match_creation_with_negative_wickets`() {
        Match.create(
            matchId = "match-invalid-2",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = -1,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_match_creation_with_overs_exceeding_twenty`() {
        Match.create(
            matchId = "match-invalid-3",
            groundId = "ground-001",
            maxOvers = 21,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_match_creation_with_wickets_exceeding_ten`() {
        Match.create(
            matchId = "match-invalid-4",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 11,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_match_creation_with_fewer_than_five_bowlers`() {
        Match.create(
            matchId = "match-invalid-5",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4")
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_InningsProgress_with_wickets_exceeding_ten`() {
        InningsProgress(
            oversCompleted = 0,
            ballsThisOver = 0,
            wicketsFallen = 11,
            currentScore = 0,
            target = null
        )
    }

    // ============================================================
    // Edge case tests
    // ============================================================

    @Test
    fun `should_create_match_with_exactly_zero_overs_boundary`() {
        val match = Match.create(
            matchId = "match-edge-1",
            groundId = "ground-001",
            maxOvers = 0,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )

        assertEquals(0, match.maxOvers)
    }

    @Test
    fun `should_create_match_with_exactly_twenty_overs_boundary`() {
        val match = Match.create(
            matchId = "match-edge-2",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )

        assertEquals(20, match.maxOvers)
    }

    @Test
    fun `should_create_match_with_exactly_ten_wickets_boundary`() {
        val match = Match.create(
            matchId = "match-edge-3",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )

        assertEquals(10, match.maxWickets)
    }

    @Test
    fun `TossResult_winner_should_be_either_Player_or_AI_never_draw`() {
        val match = Match.create(
            matchId = "match-edge-4",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5")
        )

        repeat(100) {
            val tossResult = match.performToss()
            assertTrue(
                "TossResult winner must be PLAYER or AI, never a draw",
                tossResult.winner == TossResult.Winner.PLAYER || tossResult.winner == TossResult.Winner.AI
            )
        }
    }

    @Test
    fun `should_create_match_with_target_exactly_zero_edge_case`() {
        val match = Match.create(
            matchId = "match-edge-5",
            groundId = "ground-001",
            maxOvers = 20,
            maxWickets = 10,
            bowlerRosterIds = listOf("bowler-1", "bowler-2", "bowler-3", "bowler-4", "bowler-5"),
            target = 0
        )

        assertEquals(0, match.target)
    }

    @Test
    fun `should_create_InningsProgress_with_wickets_at_boundary_ten`() {
        val progress = InningsProgress(
            oversCompleted = 15,
            ballsThisOver = 3,
            wicketsFallen = 10,
            currentScore = 120,
            target = 150
        )

        assertEquals(10, progress.wicketsFallen)
    }

    @Test
    fun `should_create_InningsProgress_with_balls_at_boundary_six`() {
        val progress = InningsProgress(
            oversCompleted = 5,
            ballsThisOver = 6,
            wicketsFallen = 2,
            currentScore = 45,
            target = null
        )

        assertEquals(6, progress.ballsThisOver)
    }
}
