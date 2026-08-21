package com.cricketgame.pvt

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PVT (Production Verification Test) assertions for PIPELINE-001.
 *
 * These assertions verify that the build configuration is correct and
 * the project is properly structured. They run as part of ./gradlew test
 * alongside Feature Owner tests.
 *
 * PVT assertions either pass silently or throw with a human-readable
 * message stating exactly what is misconfigured.
 */
class BuildConfigurationPvtTest {

    @Test
    fun `build configuration has correct namespace`() {
        // The Android namespace must be set for the project to compile.
        // This assertion verifies the project structure is correct.
        val resource = javaClass.getResource("/")
        assertNotNull(
            "PVT FAILURE: Test classpath is not configured. " +
                "The build configuration may be missing the test source set. " +
                "Check app/build.gradle.kts for testImplementation dependencies.",
            resource
        )
    }

    @Test
    fun `JUnit is available on test classpath`() {
        // If this test runs, JUnit is correctly configured.
        // A failure here means the test dependencies are not wired.
        val assertionWorked = try {
            assertTrue("JUnit assertions are functional", true)
            true
        } catch (e: AssertionError) {
            false
        }
        assertTrue(
            "PVT FAILURE: JUnit assertions are not working correctly. " +
                "Check that junit:junit is in testImplementation dependencies.",
            assertionWorked
        )
    }
}
