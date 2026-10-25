package com.example.hotel.controller;

import com.example.hotel.config.HotelControllerConfiguration;
import com.example.hotel.model.Floor;
import com.example.hotel.model.Hotel;
import com.example.hotel.model.SubCorridor;
import com.example.hotel.policy.CorridorControlPolicy;
import com.example.hotel.policy.DefaultCorridorControlPolicy;
import com.example.hotel.policy.DefaultPowerManagementPolicy;
import com.example.hotel.utilities.TestClock;
import com.example.hotel.utilities.TestHelpers;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HotelControllerTest {

    @Test
    void constructorRejectsNullHotel() {
        assertThrows(IllegalArgumentException.class,
                () -> new HotelController(null, new DefaultPowerManagementPolicy(),
                        new DefaultCorridorControlPolicy(HotelControllerConfiguration.defaults()),
                        TestHelpers.fixedClock("2026-01-01T00:00:00Z")));
    }

    @Test
    void constructorRejectsNullPowerPolicy() {
        Hotel hotel = new Hotel(1, 1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> new HotelController(hotel, null,
                        new DefaultCorridorControlPolicy(HotelControllerConfiguration.defaults()),
                        TestHelpers.fixedClock("2026-01-01T00:00:00Z")));
    }

    @Test
    void constructorRejectsNullCorridorPolicy() {
        Hotel hotel = new Hotel(1, 1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> new HotelController(hotel, new DefaultPowerManagementPolicy(),
                        null, TestHelpers.fixedClock("2026-01-01T00:00:00Z")));
    }

    @Test
    void constructorRejectsNullClock() {
        Hotel hotel = new Hotel(1, 1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> new HotelController(hotel, new DefaultPowerManagementPolicy(),
                        new DefaultCorridorControlPolicy(HotelControllerConfiguration.defaults()),
                        null));
    }

    @Test
    void initializesMainLightsOnSubLightsOffAndEnforcesCap() {
        Hotel hotel = new Hotel(2, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        for (Floor floor : hotel.getFloors()) {
            assertTrue(floor.getMainCorridors().get(0).getLight().isOn());
            assertFalse(floor.getSubCorridors().get(0).getLight().isOn());
            assertFalse(floor.getSubCorridors().get(1).getLight().isOn());
            assertTrue(floor.getActualPowerConsumption() <= floor.getPowerCap());
        }
    }

    @Test
    void motionTurnsOnTargetLightAndRecordsTime() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        controller.motionDetected(1, 2);

        SubCorridor target = hotel.getFloor(1).getSubCorridor(2);
        assertTrue(target.getLight().isOn());
        assertNotNull(target.getLastMotionAt());
        assertTrue(hotel.getFloor(1).getActualPowerConsumption()
                <= hotel.getFloor(1).getPowerCap());
    }

    @Test
    void motionDetectedRejectsInvalidFloor() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        assertThrows(IllegalArgumentException.class, () -> controller.motionDetected(2, 1));
        assertThrows(IllegalArgumentException.class, () -> controller.motionDetected(0, 1));
        assertThrows(IllegalArgumentException.class, () -> controller.motionDetected(-1, 1));
    }

    @Test
    void motionDetectedRejectsInvalidSubCorridor() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        assertThrows(IllegalArgumentException.class, () -> controller.motionDetected(1, 3));
        assertThrows(IllegalArgumentException.class, () -> controller.motionDetected(1, 0));
        assertThrows(IllegalArgumentException.class, () -> controller.motionDetected(1, -1));
    }

    @Test
    void timeoutBeforeMoreThanOneMinuteIsRejected() {
        Hotel hotel = new Hotel(1, 1, 2);
        TestClock clock = new TestClock("2026-01-01T00:00:00Z");
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                new DefaultCorridorControlPolicy(HotelControllerConfiguration.defaults()),
                clock);
        controller.motionDetected(1, 1);
        clock.setInstant("2026-01-01T00:01:00Z");

        assertThrows(IllegalStateException.class, () -> controller.inactivityTimeout(1, 1));
    }

    @Test
    void timeoutAfterMoreThanOneMinuteTurnsLightOffAndRequestsAcOn() {
        Hotel hotel = new Hotel(1, 1, 2);
        TestClock clock = new TestClock("2026-01-01T00:00:00Z");
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                new DefaultCorridorControlPolicy(HotelControllerConfiguration.defaults()),
                clock);
        controller.motionDetected(1, 2);
        clock.setInstant("2026-01-01T00:01:01Z");

        controller.inactivityTimeout(1, 2);

        SubCorridor target = hotel.getFloor(1).getSubCorridor(2);
        assertFalse(target.getLight().isOn());
        assertTrue(hotel.getFloor(1).getActualPowerConsumption()
                <= hotel.getFloor(1).getPowerCap());
    }

    @Test
    void inactivityTimeoutRejectsInvalidFloor() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        assertThrows(IllegalArgumentException.class, () -> controller.inactivityTimeout(2, 1));
    }

    @Test
    void inactivityTimeoutRejectsInvalidSubCorridor() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        assertThrows(IllegalArgumentException.class, () -> controller.inactivityTimeout(1, 3));
    }

    @Test
    void inactivityTimeoutRejectsWhenNoMotionRecorded() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        assertThrows(IllegalStateException.class, () -> controller.inactivityTimeout(1, 1));
    }

    @Test
    void simulatedInactivityImmediatelyTurnsTargetLightOffAndRequestsAcOn() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        controller.motionDetected(1, 2);
        controller.simulateInactivityTimeout(1, 2);

        SubCorridor target = hotel.getFloor(1).getSubCorridor(2);
        assertFalse(target.getLight().isOn());
        assertTrue(target.getAirConditioner().isOn());
        assertTrue(hotel.getFloor(1).getActualPowerConsumption()
                <= hotel.getFloor(1).getPowerCap());
    }

    @Test
    void simulatedInactivityRequiresPriorMotion() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        assertThrows(IllegalStateException.class,
                () -> controller.simulateInactivityTimeout(1, 2));
    }

    @Test
    void simulatedInactivityRejectsInvalidFloor() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        assertThrows(IllegalArgumentException.class, () -> controller.simulateInactivityTimeout(2, 1));
    }

    @Test
    void simulatedInactivityRejectsInvalidSubCorridor() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        assertThrows(IllegalArgumentException.class, () -> controller.simulateInactivityTimeout(1, 3));
    }

    @Test
    void configurableInactivityThresholdCanBeChangedWithoutChangingController() {
        Hotel hotel = new Hotel(1, 1, 2);
        TestClock clock = new TestClock("2026-01-01T00:00:00Z");
        CorridorControlPolicy eventPolicy = new DefaultCorridorControlPolicy(
                new HotelControllerConfiguration(120));
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(), eventPolicy, clock);

        controller.motionDetected(1, 1);
        clock.setInstant("2026-01-01T00:01:01Z");
        assertThrows(IllegalStateException.class, () -> controller.inactivityTimeout(1, 1));

        clock.setInstant("2026-01-01T00:02:01Z");
        assertDoesNotThrow(() -> controller.inactivityTimeout(1, 1));
    }

    @Test
    void mainAndSubCorridorLightsFollowNightTimeWindow() {
        Hotel hotel = new Hotel(1, 1, 2);
        TestClock clock = new TestClock("2026-01-01T12:00:00Z");
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                new DefaultCorridorControlPolicy(HotelControllerConfiguration.defaults()),
                clock);

        assertFalse(hotel.getFloor(1).getMainCorridors().get(0).getLight().isOn(),
                "Main corridor light should be off at noon");
        controller.motionDetected(1, 1);
        assertFalse(hotel.getFloor(1).getSubCorridor(1).getLight().isOn(),
                "Sub-corridor light should not turn on during daytime");

        clock.setInstant("2026-01-01T18:00:00Z");
        controller.refreshTimeBasedLighting();
        controller.motionDetected(1, 2);
        assertTrue(hotel.getFloor(1).getMainCorridors().get(0).getLight().isOn(),
                "Main corridor light should be on at 6 PM");
        assertTrue(hotel.getFloor(1).getSubCorridor(2).getLight().isOn(),
                "Sub-corridor light should turn on at night when motion is detected");
    }

    @Test
    void motionNightOverrideTurnsLightsOnEvenWhenClockSaysDaytime() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T12:00:00Z"));

        controller.motionDetected(1, 2, Boolean.TRUE);

        assertTrue(hotel.getFloor(1).getMainCorridors().get(0).getLight().isOn());
        assertTrue(hotel.getFloor(1).getSubCorridor(2).getLight().isOn());
        assertTrue(hotel.getFloor(1).getActualPowerConsumption()
                <= hotel.getFloor(1).getPowerCap());
    }

    @Test
    void motionDayOverrideKeepsLightsOffEvenWhenClockSaysNight() {
        Hotel hotel = new Hotel(1, 1, 2);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T22:00:00Z"));

        controller.motionDetected(1, 2, Boolean.FALSE);

        assertFalse(hotel.getFloor(1).getMainCorridors().get(0).getLight().isOn());
        assertFalse(hotel.getFloor(1).getSubCorridor(2).getLight().isOn());
        assertNotNull(hotel.getFloor(1).getSubCorridor(2).getLastMotionAt());
        assertTrue(hotel.getFloor(1).getActualPowerConsumption()
                <= hotel.getFloor(1).getPowerCap());
    }

    @Test
    void refreshTimeBasedLightingCanSwitchMainLightsAfterTimeBoundary() {
        Hotel hotel = new Hotel(1, 1, 1);
        TestClock clock = new TestClock("2026-01-01T17:59:00Z");
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                new DefaultCorridorControlPolicy(HotelControllerConfiguration.defaults()),
                clock);
        assertFalse(hotel.getFloor(1).getMainCorridors().get(0).getLight().isOn());

        clock.setInstant("2026-01-01T18:00:00Z");
        controller.refreshTimeBasedLighting();
        assertTrue(hotel.getFloor(1).getMainCorridors().get(0).getLight().isOn());
    }

    @Test
    void getHotelReturnsCorrectHotel() {
        Hotel hotel = new Hotel(1, 1, 1);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        assertEquals(hotel, controller.getHotel());
    }

    @Test
    void getCurrentLocalTimeReturnsCurrentTime() {
        Hotel hotel = new Hotel(1, 1, 1);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T12:00:00Z"));

        assertNotNull(controller.getCurrentLocalTime());
    }

    @Test
    void isNightTimeNowReturnsCorrectValue() {
        Hotel hotel = new Hotel(1, 1, 1);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T22:00:00Z"));

        assertTrue(controller.isNightTimeNow());
    }

    @Test
    void getTimeZoneReturnsClockZone() {
        Hotel hotel = new Hotel(1, 1, 1);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        assertNotNull(controller.getTimeZone());
    }

    @Test
    void backwardCompatibleConstructorWorks() {
        Hotel hotel = new Hotel(1, 1, 1);
        HotelController controller = new HotelController(
                hotel, new DefaultPowerManagementPolicy(),
                TestHelpers.fixedClock("2026-01-01T00:00:00Z"));

        assertNotNull(controller);
        assertEquals(hotel, controller.getHotel());
    }
}
