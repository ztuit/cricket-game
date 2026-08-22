package com.cricketgame.domain.player

/**
 * An AI-controlled fictional character who delivers the ball.
 *
 * Has type, experience, personality (nickname, quirk).
 * Bowler Type and Experience Class are immutable for the match.
 *
 * Ubiquitous language: Bowler — not "bowling character" or "pitcher".
 */
class Bowler private constructor(
    val bowlerId: String,
    val bowlerType: BowlerType,
    val experienceClass: ExperienceClass,
    val nickname: String,
    val quirk: String,
    val stats: BowlerStats,
    val oversBowled: Int = 0
) {
    companion object {
        /**
         * Create a Bowler with stats derived from type and experience.
         */
        fun create(
            bowlerId: String,
            bowlerType: BowlerType,
            experienceClass: ExperienceClass,
            nickname: String,
            quirk: String
        ): Bowler {
            require(nickname.isNotBlank()) { "nickname must not be empty" }
            require(quirk.isNotBlank()) { "quirk must not be empty" }

            val stats = deriveStats(bowlerType, experienceClass)

            return Bowler(
                bowlerId = bowlerId,
                bowlerType = bowlerType,
                experienceClass = experienceClass,
                nickname = nickname,
                quirk = quirk,
                stats = stats
            )
        }

        /**
         * Derive bowler stats from type and experience class.
         * Higher experience = better accuracy, lower error rates.
         */
        private fun deriveStats(type: BowlerType, experience: ExperienceClass): BowlerStats {
            val baseSkill = when (type) {
                BowlerType.FAST -> 0.7f
                BowlerType.MEDIUM_FAST -> 0.6f
                BowlerType.OFF_SPIN -> 0.65f
                BowlerType.LEG_SPIN -> 0.6f
            }

            val experienceMultiplier = when (experience) {
                ExperienceClass.ROOKIE -> 0.8f
                ExperienceClass.ESTABLISHED -> 1.0f
                ExperienceClass.ELITE -> 1.2f
            }

            val skill = (baseSkill * experienceMultiplier).coerceIn(0.0f, 1.0f)
            val accuracy = (0.5f * experienceMultiplier).coerceIn(0.0f, 1.0f)
            val variation = when (type) {
                BowlerType.FAST -> 0.4f
                BowlerType.MEDIUM_FAST -> 0.5f
                BowlerType.OFF_SPIN -> 0.6f
                BowlerType.LEG_SPIN -> 0.7f
            }
            val wideRate = (0.15f / experienceMultiplier).coerceIn(0.0f, 1.0f)
            val noBallRate = (0.1f / experienceMultiplier).coerceIn(0.0f, 1.0f)

            return BowlerStats(
                bowlingSkill = skill,
                accuracy = accuracy,
                variation = variation,
                wideRate = wideRate,
                noBallRate = noBallRate
            )
        }
    }
}
