package com.cricketgame.domain.pitch

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for Pitch, Ground, SurfaceCondition, and Weather domain objects.
 *
 * Ubiquitous language terms (from ddd.md):
 * - Pitch: The prepared strip of ground where the ball bounces, divided into zones
 * - Ground: The cricket venue (name, location, default weather)
 * - SurfaceCondition: State of a zone on the pitch (degradation, moisture, roughness)
 * - Weather: Match-level atmospheric condition (Sunny, Overcast, Humid, Cloudy)
 */
class PitchTest {

    // ============================================================
    // Happy path tests — Ground
    // ============================================================

    @Test
    fun `should_create_Ground_with_name_location_and_default_weather`() {
        val ground = Ground.create(
            groundId = "ground-1",
            name = "Lord's",
            location = "London, England",
            defaultWeather = Weather(
                condition = WeatherCondition.OVERCAST,
                temperature = 18.0f,
                humidity = 0.65f
            )
        )

        assertEquals("ground-1", ground.groundId)
        assertEquals("Lord's", ground.name)
        assertEquals("London, England", ground.location)
        assertEquals(WeatherCondition.OVERCAST, ground.defaultWeather.condition)
        assertEquals(18.0f, ground.defaultWeather.temperature, 0.01f)
        assertTrue("Humidity should be in [0,1]", ground.defaultWeather.humidity in 0.0f..1.0f)
    }

    // ============================================================
    // Happy path tests — Weather
    // ============================================================

    @Test
    fun `should_create_Weather_with_valid_condition`() {
        val sunny = Weather(WeatherCondition.SUNNY, 30.0f, 0.3f)
        val overcast = Weather(WeatherCondition.OVERCAST, 20.0f, 0.7f)
        val humid = Weather(WeatherCondition.HUMID, 28.0f, 0.85f)
        val cloudy = Weather(WeatherCondition.CLOUDY, 22.0f, 0.5f)

        assertEquals(WeatherCondition.SUNNY, sunny.condition)
        assertEquals(WeatherCondition.OVERCAST, overcast.condition)
        assertEquals(WeatherCondition.HUMID, humid.condition)
        assertEquals(WeatherCondition.CLOUDY, cloudy.condition)
    }

    // ============================================================
    // Happy path tests — Pitch and SurfaceCondition
    // ============================================================

    @Test
    fun `should_create_Pitch_with_zone_grid_and_SurfaceCondition_per_zone`() {
        val zones = listOf(
            SurfaceCondition("zone-1", degradation = 0.0f, moisture = 0.5f, roughness = 0.1f),
            SurfaceCondition("zone-2", degradation = 0.0f, moisture = 0.5f, roughness = 0.1f),
            SurfaceCondition("zone-3", degradation = 0.0f, moisture = 0.5f, roughness = 0.1f)
        )

        val pitch = Pitch.create(
            pitchId = "pitch-1",
            groundId = "ground-1",
            weather = Weather(WeatherCondition.SUNNY, 25.0f, 0.4f),
            zones = zones
        )

        assertEquals("pitch-1", pitch.pitchId)
        assertEquals("ground-1", pitch.groundId)
        assertEquals(3, pitch.zones.size)
        assertEquals(0, pitch.ballAge)
    }

    @Test
    fun `should_create_SurfaceCondition_with_all_attributes_in_valid_range`() {
        val condition = SurfaceCondition(
            zoneId = "zone-1",
            degradation = 0.3f,
            moisture = 0.7f,
            roughness = 0.2f
        )

        assertEquals("zone-1", condition.zoneId)
        assertTrue("Degradation should be in [0,1]", condition.degradation in 0.0f..1.0f)
        assertTrue("Moisture should be in [0,1]", condition.moisture in 0.0f..1.0f)
        assertTrue("Roughness should be in [0,1]", condition.roughness in 0.0f..1.0f)
    }

    @Test
    fun `should_initialize_Pitch_with_ballAge_zero`() {
        val pitch = Pitch.create(
            pitchId = "pitch-new",
            groundId = "ground-1",
            weather = Weather(WeatherCondition.SUNNY, 25.0f, 0.4f),
            zones = listOf(
                SurfaceCondition("z1", 0.0f, 0.5f, 0.1f),
                SurfaceCondition("z2", 0.0f, 0.5f, 0.1f)
            )
        )

        assertEquals(0, pitch.ballAge)
    }

    // ============================================================
    // Unhappy path tests — SurfaceCondition range checks (SEC-003)
    // ============================================================

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_SurfaceCondition_with_degradation_below_zero`() {
        SurfaceCondition("zone-bad", degradation = -0.1f, moisture = 0.5f, roughness = 0.1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_SurfaceCondition_with_degradation_above_one`() {
        SurfaceCondition("zone-bad", degradation = 1.1f, moisture = 0.5f, roughness = 0.1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_SurfaceCondition_with_moisture_below_zero`() {
        SurfaceCondition("zone-bad", degradation = 0.0f, moisture = -0.1f, roughness = 0.1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_SurfaceCondition_with_moisture_above_one`() {
        SurfaceCondition("zone-bad", degradation = 0.0f, moisture = 1.1f, roughness = 0.1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_SurfaceCondition_with_roughness_below_zero`() {
        SurfaceCondition("zone-bad", degradation = 0.0f, moisture = 0.5f, roughness = -0.1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_SurfaceCondition_with_roughness_above_one`() {
        SurfaceCondition("zone-bad", degradation = 0.0f, moisture = 0.5f, roughness = 1.1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_Weather_with_humidity_below_zero`() {
        Weather(WeatherCondition.SUNNY, 25.0f, humidity = -0.1f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should_reject_Weather_with_humidity_above_one`() {
        Weather(WeatherCondition.SUNNY, 25.0f, humidity = 1.1f)
    }

    // ============================================================
    // Edge case tests — SurfaceCondition boundaries
    // ============================================================

    @Test
    fun `should_create_SurfaceCondition_with_degradation_at_zero_boundary`() {
        val condition = SurfaceCondition("zone-edge-1", degradation = 0.0f, moisture = 0.0f, roughness = 0.0f)

        assertEquals(0.0f, condition.degradation, 0.001f)
        assertEquals(0.0f, condition.moisture, 0.001f)
        assertEquals(0.0f, condition.roughness, 0.001f)
    }

    @Test
    fun `should_create_SurfaceCondition_with_all_values_at_one_boundary`() {
        val condition = SurfaceCondition("zone-edge-2", degradation = 1.0f, moisture = 1.0f, roughness = 1.0f)

        assertEquals(1.0f, condition.degradation, 0.001f)
        assertEquals(1.0f, condition.moisture, 0.001f)
        assertEquals(1.0f, condition.roughness, 0.001f)
    }

    @Test
    fun `should_create_Pitch_with_single_zone_minimum`() {
        val pitch = Pitch.create(
            pitchId = "pitch-min",
            groundId = "ground-1",
            weather = Weather(WeatherCondition.SUNNY, 25.0f, 0.4f),
            zones = listOf(SurfaceCondition("z1", 0.0f, 0.5f, 0.1f))
        )

        assertEquals(1, pitch.zones.size)
    }
}
