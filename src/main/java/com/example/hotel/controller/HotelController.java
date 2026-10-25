
package com.example.hotel.controller;

import com.example.hotel.config.HotelControllerConfiguration;
import com.example.hotel.model.Floor;
import com.example.hotel.model.Hotel;
import com.example.hotel.model.MainCorridor;
import com.example.hotel.model.SubCorridor;
import com.example.hotel.policy.CorridorControlPolicy;
import com.example.hotel.policy.DefaultCorridorControlPolicy;
import com.example.hotel.policy.PowerManagementPolicy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public final class HotelController {

    private final Hotel hotel;
    private final PowerManagementPolicy powerPolicy;
    private final CorridorControlPolicy corridorControlPolicy;
    private final Clock clock;

    public HotelController(
            Hotel hotel,
            PowerManagementPolicy powerPolicy,
            Clock clock) {

        this(
                hotel,
                powerPolicy,
                new DefaultCorridorControlPolicy(
                        HotelControllerConfiguration.defaults()),
                clock);
    }

    public HotelController(
            Hotel hotel,
            PowerManagementPolicy powerPolicy,
            CorridorControlPolicy corridorControlPolicy,
            Clock clock) {

        if (hotel == null
                || powerPolicy == null
                || corridorControlPolicy == null
                || clock == null) {

            throw new IllegalArgumentException(
                    "Hotel, power policy, corridor control policy "
                            + "and clock are required");
        }

        this.hotel = hotel;
        this.powerPolicy = powerPolicy;
        this.corridorControlPolicy = corridorControlPolicy;
        this.clock = clock;

        refreshTimeBasedLighting();

        for (Floor floor : hotel.getFloors()) {
            powerPolicy.enforceLimit(floor);
        }
    }

    public Hotel getHotel() {
        return hotel;
    }

    public LocalTime getCurrentLocalTime() {
        return Instant.now(clock)
                .atZone(clock.getZone())
                .toLocalTime();
    }

    public boolean isNightTimeNow() {
        return corridorControlPolicy.isNightTime(Instant.now(clock));
    }

    public ZoneId getTimeZone() {
        return clock.getZone();
    }

    public void motionDetected(
            int floorNumber,
            int subCorridorNumber) {

        motionDetected(floorNumber, subCorridorNumber, null);
    }

    public void motionDetected(
            int floorNumber,
            int subCorridorNumber,
            Boolean nightOverride) {

        if (nightOverride == null) {
            refreshTimeBasedLighting();
        } else {
            refreshTimeBasedLighting(nightOverride.booleanValue());
        }

        Floor floor = hotel.getFloor(floorNumber);
        SubCorridor corridor =
                floor.getSubCorridor(subCorridorNumber);

        Instant now = Instant.now(clock);

        if (nightOverride == null) {
            corridorControlPolicy.onMotionDetected(corridor, now);
        } else {
            corridorControlPolicy.onMotionDetected(
                    corridor,
                    now,
                    nightOverride.booleanValue());
        }

        enforceAllFloorLimits();
    }

    public void inactivityTimeout(
            int floorNumber,
            int subCorridorNumber) {

        refreshTimeBasedLighting();

        Floor floor = hotel.getFloor(floorNumber);
        SubCorridor corridor =
                floor.getSubCorridor(subCorridorNumber);

        Instant lastMotion = corridor.getLastMotionAt();

        if (lastMotion == null) {
            throw new IllegalStateException(
                    "No motion has been recorded for this corridor");
        }

        if (!corridorControlPolicy.isInactivityTimeoutDue(
                lastMotion, Instant.now(clock))) {

            throw new IllegalStateException(
                    "Inactivity threshold has not been exceeded");
        }

        applyInactivityState(floor, corridor);
    }

    public void simulateInactivityTimeout(
            int floorNumber,
            int subCorridorNumber) {

        refreshTimeBasedLighting();

        Floor floor = hotel.getFloor(floorNumber);
        SubCorridor corridor =
                floor.getSubCorridor(subCorridorNumber);

        if (corridor.getLastMotionAt() == null) {
            throw new IllegalStateException(
                    "Detect motion first before simulating inactivity");
        }

        applyInactivityState(floor, corridor);
    }

    public void refreshTimeBasedLighting() {
        boolean nightTime =
                corridorControlPolicy.isNightTime(Instant.now(clock));

        refreshTimeBasedLighting(nightTime);
    }

    private void refreshTimeBasedLighting(boolean nightTime) {

        for (Floor floor : hotel.getFloors()) {
            for (MainCorridor corridor : floor.getMainCorridors()) {
                if (nightTime) {
                    corridor.getLight().turnOn();
                } else {
                    corridor.getLight().turnOff();
                }
            }
        }
    }

    private void applyInactivityState(
            Floor floor,
            SubCorridor corridor) {

        corridorControlPolicy.onInactivityTimeout(corridor);
        enforceAllFloorLimits();
    }

    private void enforceAllFloorLimits() {

        for (Floor currentFloor : hotel.getFloors()) {
            powerPolicy.enforceLimit(currentFloor);
        }
    }
}
