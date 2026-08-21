package com.cricketgame.domain.player

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for Bowler domain object and Bowler roster.
 *
 * Ubiquitous language terms (from ddd.md):
 * - Bowler: AI-controlled fictional character who delivers the ball
 * - BowlerType: Fast, MediumFast, OffSpin, LegSpin
 * - ExperienceClass: Rookie, Established, Elite
 * - Nickname: Character name (e.g., "The Magician")
 * - Quirk: Non-cricket personality trait
 * - BowlerStats: bowlingSkill, accuracy, variation, wideRate, noBallRate
 */
class BowlerTest {

    // ============================================================
    // Happy path tests
    // ============================================================

    @Test
    fun `should_create_bowler_with_valid_bowlerType_and_experienceClass`() {
        val bowler = Bowler.create(
            bowlerId = "bowler-1",
            bowlerType = BowlerType.FAST,
            experienceClass = ExperienceClass.ELITE,
            nickname = "The Express",
            quirk = "Celebrates with a backflip"
        )

        assertEquals("bowler-1", bowler.bowlerId)
        assertEquals(BowlerType.FAST, bowler.bowlerType)
        assertEquals(ExperienceClass.ELITE, bowler.experienceClass)
        assertEquals("The Express", bowler.nickname)
        assertEquals("Celebrates with a backflip", bowler.quirk)
    }

    @Test
    fun `should_create_bowler_with_all_four_bowlerTypes`() {
        val fast = Bowler.create("b1", BowlerType.FAST, ExperienceClass.ROOKIE, "Speedy", "Runs fast")
        val mediumFast = Bowler.create("b2", BowlerType.MEDIUM_FAST, ExperienceClass.ESTABLISHED, "Steady", "Calm")
        val offSpin = Bowler.create("b3", BowlerType.OFF_SPIN, ExperienceClass.ELITE, "Spinner", "Witty")
        val legSpin = Bowler.create("b4", BowlerType.LEG_SPIN, ExperienceClass.ROOKIE, "Magician", "Mysterious")

        assertEquals(BowlerType.FAST, fast.bowlerType)
        assertEquals(BowlerType.MEDIUM_FAST, mediumFast.bowlerType)
        assertEquals(BowlerType.OFF_SPIN, offSpin.bowlerType)
        assertEquals(BowlerType.LEG_SPIN, legSpin.bowlerType)
    }

    @Test
    fun `should_populate_bowler_roster_with_at_least_five_bowlers_covering_all_bowlerTypes`() {
        val roster = BowlerRoster.create(
            bowlers = listOf(
                Bowler.create("b1", BowlerType.FAST, ExperienceClass.ELITE, "Speed1", "q1"),
                Bowler.create("b2", BowlerType.FAST, ExperienceClass.ROOKIE, "Speed2", "q2"),
                Bowler.create("b3", BowlerType.MEDIUM_FAST, ExperienceClass.ESTABLISHED, "Med1", "q3"),
                Bowler.create("b4", BowlerType.OFF_SPIN, ExperienceClass.ELITE, "Spin1", "q4"),
                Bowler.create("b5", BowlerType.LEG_SPIN, ExperienceClass.ROOKIE, "Leg1", "q5")
            )
        )

        assertEquals(5, roster.bowlers.size)
        assertTrue("Roster should cover Fast", roster.coversBowlerType(BowlerType.FAST))
        assertTrue("Roster should cover MediumFast", roster.coversBowlerType(BowlerType.MEDIUM_FAST))
        assertTrue("Roster should cover OffSpin", roster.coversBowlerType(BowlerType.OFF_SPIN))
        assertTrue("Roster should cover LegSpin", roster.coversBowlerType(BowlerType.LEG_SPIN))
    }

    @Test
    fun `should_have_bowlerStats_with_all_attributes_in_valid_range`() {
        val bowler = Bowler.create(
            bowlerId = "bowler-stats",
            bowlerType = BowlerType.FAST,
            experienceClass = ExperienceClass.ELITE,
            nickname = "Stats Man",
            quirk = "Loves numbers"
        )

        val stats = bowler.stats

        assertTrue("bowlingSkill should be in [0,1]", stats.bowlingSkill in 0.0f..1.0f)
        assertTrue("accuracy should be in [0,1]", stats.accuracy in 0.0f..1.0f)
        assertTrue("variation should be in [0,1]", stats.variation in 0.0f..1.0f)
        assertTrue("wideRate should be in [0,1]", stats.wideRate in 0.0f..1.0f)
        assertTrue("noBallRate should be in [0,1]", stats.noBallRate in 0.0f..1.0f)
    }

    @Test
    fun `should_initialize_bowler_with_zero_overs_bowled`() {
        val bowler = Bowler.create(
            bowlerId = "bowler-new",
            bowlerType = BowlerType.MEDIUM_FAST,
            experienceClass = ExperienceClass.ROOKIE,
            nickname = "Newbie",
            quirk = "Nervous"
        )

        assertEquals(0, bowler.oversBowled)
    }

    // ============================================================
    // Batsman tests
    // ============================================================

    @Test
    fun `should_create_batsman_with_valid_stats`() {
        val batsman = Batsman.create(
            batsmanId = "batsman-1",
            nickname = "The Wall",
            quirk = "Taps bat three times"
        )

        assertEquals("batsman-1", batsman.batsmanId)
        assertNotNull("Batsman stats should be set", batsman.stats)
    }

    @Test
    fun `should_have_batsmanStats_with_all_attributes_in_valid_range`() {
        val batsman = Batsman.create(
            batsmanId = "batsman-stats",
            nickname = "Power Hitter",
            quirk = "Points to the sky"
        )

        val stats = batsman.stats

        assertTrue("battingSkill should be in [0,1]", stats.battingSkill in 0.0f..1.0f)
        assertTrue("timing should be in [0,1]", stats.timing in 0.0f..1.0f)
        assertTrue("power should be in [0,1]", stats.power in 0.0f..1.0f)
        assertTrue("composure should be in [0,1]", stats.composure in 0.0f..1.0f)
    }

    // ============================================================
    // Unhappy path tests
    // ============================================================

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_bowler_roster_with_fewer_than_five_bowlers`() {
        BowlerRoster.create(
            bowlers = listOf(
                Bowler.create("b1", BowlerType.FAST, ExperienceClass.ELITE, "S1", "q1"),
                Bowler.create("b2", BowlerType.MEDIUM_FAST, ExperienceClass.ROOKIE, "S2", "q2"),
                Bowler.create("b3", BowlerType.OFF_SPIN, ExperienceClass.ESTABLISHED, "S3", "q3"),
                Bowler.create("b4", BowlerType.LEG_SPIN, ExperienceClass.ELITE, "S4", "q4")
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_bowler_roster_missing_a_bowlerType`() {
        BowlerRoster.create(
            bowlers = listOf(
                Bowler.create("b1", BowlerType.FAST, ExperienceClass.ELITE, "S1", "q1"),
                Bowler.create("b2", BowlerType.FAST, ExperienceClass.ROOKIE, "S2", "q2"),
                Bowler.create("b3", BowlerType.FAST, ExperienceClass.ESTABLISHED, "S3", "q3"),
                Bowler.create("b4", BowlerType.MEDIUM_FAST, ExperienceClass.ELITE, "S4", "q4"),
                Bowler.create("b5", BowlerType.MEDIUM_FAST, ExperienceClass.ROOKIE, "S5", "q5")
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_bowler_with_empty_nickname`() {
        Bowler.create(
            bowlerId = "bowler-bad",
            bowlerType = BowlerType.FAST,
            experienceClass = ExperienceClass.ROOKIE,
            nickname = "",
            quirk = "Some quirk"
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_bowler_with_empty_quirk`() {
        Bowler.create(
            bowlerId = "bowler-bad2",
            bowlerType = BowlerType.FAST,
            experienceClass = ExperienceClass.ROOKIE,
            nickname = "Some Name",
            quirk = ""
        )
    }

    // ============================================================
    // Edge case tests
    // ============================================================

    @Test
    fun `should_create_bowler_roster_with_exactly_five_bowlers`() {
        val roster = BowlerRoster.create(
            bowlers = listOf(
                Bowler.create("b1", BowlerType.FAST, ExperienceClass.ELITE, "S1", "q1"),
                Bowler.create("b2", BowlerType.MEDIUM_FAST, ExperienceClass.ROOKIE, "S2", "q2"),
                Bowler.create("b3", BowlerType.OFF_SPIN, ExperienceClass.ESTABLISHED, "S3", "q3"),
                Bowler.create("b4", BowlerType.LEG_SPIN, ExperienceClass.ELITE, "S4", "q4"),
                Bowler.create("b5", BowlerType.FAST, ExperienceClass.ROOKIE, "S5", "q5")
            )
        )

        assertEquals(5, roster.bowlers.size)
    }

    @Test
    fun `should_create_bowler_with_all_three_experienceClasses`() {
        val rookie = Bowler.create("b1", BowlerType.FAST, ExperienceClass.ROOKIE, "R", "q")
        val established = Bowler.create("b2", BowlerType.FAST, ExperienceClass.ESTABLISHED, "E", "q")
        val elite = Bowler.create("b3", BowlerType.FAST, ExperienceClass.ELITE, "L", "q")

        assertEquals(ExperienceClass.ROOKIE, rookie.experienceClass)
        assertEquals(ExperienceClass.ESTABLISHED, established.experienceClass)
        assertEquals(ExperienceClass.ELITE, elite.experienceClass)
    }
}
