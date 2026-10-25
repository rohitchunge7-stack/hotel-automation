package com.example.hotel.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class HotelControllerConfigurationTest {

    @Test
    void defaultsCreatesConfigurationWithDefaultThreshold() {
        HotelControllerConfiguration config = HotelControllerConfiguration.defaults();

        assertEquals(60L, config.getInactivityThresholdSeconds());
        assertEquals(Duration.ofSeconds(60), config.getInactivityThreshold());
    }

    @Test
    void constructorAcceptsValidThreshold() {
        HotelControllerConfiguration config = new HotelControllerConfiguration(120);

        assertEquals(120L, config.getInactivityThresholdSeconds());
        assertEquals(Duration.ofSeconds(120), config.getInactivityThreshold());
    }

    @Test
    void constructorRejectsNegativeThreshold() {
        assertThrows(IllegalArgumentException.class, () -> new HotelControllerConfiguration(-1));
    }

    @Test
    void constructorAcceptsZeroThreshold() {
        HotelControllerConfiguration config = new HotelControllerConfiguration(0);

        assertEquals(0L, config.getInactivityThresholdSeconds());
    }

    @Test
    void constructorAcceptsLargeThreshold() {
        HotelControllerConfiguration config = new HotelControllerConfiguration(86400); // 24 hours

        assertEquals(86400L, config.getInactivityThresholdSeconds());
    }

    @Test
    void getInactivityThresholdReturnsCorrectDuration() {
        HotelControllerConfiguration config = new HotelControllerConfiguration(30);

        assertEquals(Duration.ofSeconds(30), config.getInactivityThreshold());
    }
}
