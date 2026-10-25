package com.example.hotel.policy;

import com.example.hotel.config.HotelControllerConfiguration;
import com.example.hotel.model.SubCorridor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public final class DefaultCorridorControlPolicy
        implements CorridorControlPolicy {

    private final Duration inactivityThreshold;

    public DefaultCorridorControlPolicy(
            HotelControllerConfiguration configuration) {

        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Configuration is required");
        }

        this.inactivityThreshold =
                configuration.getInactivityThreshold();
    }

    @Override
    public void onMotionDetected(
            SubCorridor corridor,
            Instant motionTime) {

        if (motionTime == null) {
            throw new IllegalArgumentException(
                    "Motion time is required");
        }

        onMotionDetected(
                corridor,
                motionTime,
                isNightTime(motionTime));
    }

    @Override
    public void onMotionDetected(
            SubCorridor corridor,
            Instant motionTime,
            boolean nightTime) {

        if (corridor == null || motionTime == null) {
            throw new IllegalArgumentException(
                    "Corridor and motion time are required");
        }

        corridor.recordMotion(motionTime, nightTime);
    }

    @Override
    public boolean isInactivityTimeoutDue(
            Instant lastMotionTime,
            Instant currentTime) {

        if (lastMotionTime == null || currentTime == null) {
            return false;
        }

        Duration inactiveFor =
                Duration.between(lastMotionTime, currentTime);

        return inactiveFor.compareTo(inactivityThreshold) > 0;
    }

    @Override
    public boolean isNightTime(Instant currentTime) {

        if (currentTime == null) {
            throw new IllegalArgumentException(
                    "Current time is required");
        }

        LocalTime time = currentTime
                .atZone(ZoneId.systemDefault())
                .toLocalTime();

        return !time.isBefore(LocalTime.of(18, 0))
                || time.isBefore(LocalTime.of(6, 0));
    }

    @Override
    public void onInactivityTimeout(SubCorridor corridor) {

        if (corridor == null) {
            throw new IllegalArgumentException(
                    "Corridor is required");
        }

        corridor.getLight().turnOff();
        corridor.getAirConditioner().turnOn();
    }
}
