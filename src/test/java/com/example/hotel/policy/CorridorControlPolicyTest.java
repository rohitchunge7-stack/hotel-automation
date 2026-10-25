package com.example.hotel.policy;

import com.example.hotel.config.HotelControllerConfiguration;
import com.example.hotel.model.SubCorridor;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CorridorControlPolicyTest {

    @Test
    void isNightTimeReturnsTrueAt18Hours() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());

        assertFalse(policy.isNightTime(Instant.parse("2026-01-01T06:00:00Z")));
        assertTrue(policy.isNightTime(Instant.parse("2026-01-01T18:00:00Z")));
    }

    @Test
    void isNightTimeReturnsTrueDuringNightHours() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());

        // Night: 18:00 inclusive to 06:00 exclusive
        assertTrue(policy.isNightTime(Instant.parse("2026-01-01T18:00:00Z")));
        assertTrue(policy.isNightTime(Instant.parse("2026-01-01T20:00:00Z")));
        assertTrue(policy.isNightTime(Instant.parse("2026-01-01T00:00:00Z")));
        assertTrue(policy.isNightTime(Instant.parse("2026-01-01T05:59:00Z")));
    }

    @Test
    void isNightTimeReturnsFalseDuringDayHours() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());

        // Day: 06:00 inclusive to 18:00 exclusive
        assertFalse(policy.isNightTime(Instant.parse("2026-01-01T06:00:00Z")));
        assertFalse(policy.isNightTime(Instant.parse("2026-01-01T12:00:00Z")));
        assertFalse(policy.isNightTime(Instant.parse("2026-01-01T17:59:00Z")));
    }

    @Test
    void isInactivityTimeoutDueReturnsFalseWhenTimeNotExceeded() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                new HotelControllerConfiguration(60));

        Instant lastMotion = Instant.parse("2026-01-01T00:00:00Z");
        Instant currentTime = Instant.parse("2026-01-01T00:01:00Z");

        assertFalse(policy.isInactivityTimeoutDue(lastMotion, currentTime));
    }

    @Test
    void isInactivityTimeoutDueReturnsTrueWhenTimeExceeded() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                new HotelControllerConfiguration(60));

        Instant lastMotion = Instant.parse("2026-01-01T00:00:00Z");
        Instant currentTime = Instant.parse("2026-01-01T00:01:01Z");

        assertTrue(policy.isInactivityTimeoutDue(lastMotion, currentTime));
    }

    @Test
    void isInactivityTimeoutDueReturnsFalseWithNullTimes() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());

        assertFalse(policy.isInactivityTimeoutDue(null, Instant.now()));
        assertFalse(policy.isInactivityTimeoutDue(Instant.now(), null));
        assertFalse(policy.isInactivityTimeoutDue(null, null));
    }

    @Test
    void onMotionDetectedRecordsMotionWithNightTime() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());
        SubCorridor corridor = new SubCorridor(1);
        Instant motionTime = Instant.parse("2026-01-01T20:00:00Z");

        policy.onMotionDetected(corridor, motionTime);

        assertNotNull(corridor.getLastMotionAt());
        assertEquals(motionTime, corridor.getLastMotionAt());
    }

    @Test
    void onMotionDetectedRecordsMotionWithDayTime() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());
        SubCorridor corridor = new SubCorridor(1);
        Instant motionTime = Instant.parse("2026-01-01T12:00:00Z");

        policy.onMotionDetected(corridor, motionTime);

        assertNotNull(corridor.getLastMotionAt());
        assertEquals(motionTime, corridor.getLastMotionAt());
    }

    @Test
    void onMotionDetectedWithExplicitNightOverride() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());
        SubCorridor corridor = new SubCorridor(1);
        Instant motionTime = Instant.parse("2026-01-01T12:00:00Z"); // Daytime

        policy.onMotionDetected(corridor, motionTime, true);

        assertNotNull(corridor.getLastMotionAt());
        assertEquals(motionTime, corridor.getLastMotionAt());
    }

    @Test
    void onMotionDetectedWithExplicitDayOverride() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());
        SubCorridor corridor = new SubCorridor(1);
        Instant motionTime = Instant.parse("2026-01-01T20:00:00Z"); // Nighttime

        policy.onMotionDetected(corridor, motionTime, false);

        assertNotNull(corridor.getLastMotionAt());
        assertEquals(motionTime, corridor.getLastMotionAt());
    }

    @Test
    void onMotionDetectedRejectsNullMotionTime() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());
        SubCorridor corridor = new SubCorridor(1);

        assertThrows(IllegalArgumentException.class,
                () -> policy.onMotionDetected(corridor, null));
    }

    @Test
    void onMotionDetectedWithOverrideRejectsNullCorridor() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());

        assertThrows(IllegalArgumentException.class,
                () -> policy.onMotionDetected(null, Instant.now(), true));
    }

    @Test
    void onMotionDetectedWithOverrideRejectsNullMotionTime() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());
        SubCorridor corridor = new SubCorridor(1);

        assertThrows(IllegalArgumentException.class,
                () -> policy.onMotionDetected(corridor, null, true));
    }

    @Test
    void onInactivityTimeoutTurnsLightOffAndAcOn() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());
        SubCorridor corridor = new SubCorridor(1);
        corridor.getLight().turnOn();
        corridor.getAirConditioner().turnOff();

        policy.onInactivityTimeout(corridor);

        assertFalse(corridor.getLight().isOn());
        assertTrue(corridor.getAirConditioner().isOn());
    }

    @Test
    void onInactivityTimeoutRejectsNullCorridor() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());

        assertThrows(IllegalArgumentException.class,
                () -> policy.onInactivityTimeout(null));
    }

    @Test
    void isNightTimeRejectsNullTime() {
        DefaultCorridorControlPolicy policy = new DefaultCorridorControlPolicy(
                HotelControllerConfiguration.defaults());

        assertThrows(IllegalArgumentException.class,
                () -> policy.isNightTime(null));
    }

    @Test
    void constructorRejectsNullConfiguration() {
        assertThrows(IllegalArgumentException.class,
                () -> new DefaultCorridorControlPolicy(null));
    }
}
