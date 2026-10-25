package com.example.hotel.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public final class HotelControllerConfiguration {
    private static final long DEFAULT_INACTIVITY_THRESHOLD_SECONDS = 60L;

    private final long inactivityThresholdSeconds;

    public HotelControllerConfiguration(long inactivityThresholdSeconds) {
        if (inactivityThresholdSeconds < 0) {
            throw new IllegalArgumentException("Inactivity threshold must not be negative");
        }
        this.inactivityThresholdSeconds = inactivityThresholdSeconds;
    }

    public static HotelControllerConfiguration defaults() {
        return new HotelControllerConfiguration(DEFAULT_INACTIVITY_THRESHOLD_SECONDS);
    }

    public static HotelControllerConfiguration load() {
        Properties properties = new Properties();
        try (InputStream input = HotelControllerConfiguration.class
                .getClassLoader().getResourceAsStream("hotel-controller.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load hotel-controller.properties", ex);
        }

        String configuredValue = properties.getProperty(
                "inactivity.threshold.seconds",
                String.valueOf(DEFAULT_INACTIVITY_THRESHOLD_SECONDS));
        try {
            return new HotelControllerConfiguration(Long.parseLong(configuredValue.trim()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "inactivity.threshold.seconds must be a whole number", ex);
        }
    }

    public long getInactivityThresholdSeconds() {
        return inactivityThresholdSeconds;
    }

    public Duration getInactivityThreshold() {
        return Duration.ofSeconds(inactivityThresholdSeconds);
    }
}
