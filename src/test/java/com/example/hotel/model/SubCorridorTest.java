package com.example.hotel.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SubCorridorTest {

    @Test
    void constructorCreatesCorridorWithCorrectNumber() {
        SubCorridor corridor = new SubCorridor(5);

        assertEquals(5, corridor.getNumber());
    }

    @Test
    void lightIsInitiallyOff() {
        SubCorridor corridor = new SubCorridor(1);

        assertFalse(corridor.getLight().isOn());
    }

    @Test
    void airConditionerIsInitiallyOn() {
        SubCorridor corridor = new SubCorridor(1);

        assertTrue(corridor.getAirConditioner().isOn());
    }

    @Test
    void getTypeReturnsSub() {
        SubCorridor corridor = new SubCorridor(1);

        assertEquals("SUB", corridor.getType());
    }

    @Test
    void recordMotionWithNightTimeTurnsLightOn() {
        SubCorridor corridor = new SubCorridor(1);
        Instant motionTime = Instant.now();

        corridor.recordMotion(motionTime, true);

        assertTrue(corridor.getLight().isOn());
    }

    @Test
    void recordMotionWithDayTimeKeepsLightOff() {
        SubCorridor corridor = new SubCorridor(1);
        Instant motionTime = Instant.now();

        corridor.recordMotion(motionTime, false);

        assertFalse(corridor.getLight().isOn());
    }

    @Test
    void recordMotionTurnsAirConditionerOn() {
        SubCorridor corridor = new SubCorridor(1);
        corridor.getAirConditioner().turnOff();
        Instant motionTime = Instant.now();

        corridor.recordMotion(motionTime, true);

        assertTrue(corridor.getAirConditioner().isOn());
    }

    @Test
    void recordMotionRecordsTimestamp() {
        SubCorridor corridor = new SubCorridor(1);
        Instant motionTime = Instant.parse("2026-01-01T12:00:00Z");

        corridor.recordMotion(motionTime, true);

        assertEquals(motionTime, corridor.getLastMotionAt());
    }

    @Test
    void recordMotionRejectsNullTime() {
        SubCorridor corridor = new SubCorridor(1);

        assertThrows(IllegalArgumentException.class,
                () -> corridor.recordMotion(null, true));
    }

    @Test
    void getLastMotionAtReturnsNullInitially() {
        SubCorridor corridor = new SubCorridor(1);

        assertNull(corridor.getLastMotionAt());
    }

    @Test
    void getLastMotionAtReturnsRecordedTime() {
        SubCorridor corridor = new SubCorridor(1);
        Instant motionTime = Instant.now();

        corridor.recordMotion(motionTime, true);

        assertEquals(motionTime, corridor.getLastMotionAt());
    }

    @Test
    void recordMotionCanUpdateTimestamp() {
        SubCorridor corridor = new SubCorridor(1);
        Instant firstMotion = Instant.parse("2026-01-01T12:00:00Z");
        Instant secondMotion = Instant.parse("2026-01-01T13:00:00Z");

        corridor.recordMotion(firstMotion, true);
        corridor.recordMotion(secondMotion, true);

        assertEquals(secondMotion, corridor.getLastMotionAt());
    }

    @Test
    void powerConsumptionIncludesLightAndAc() {
        SubCorridor corridor = new SubCorridor(1);

        int consumption = corridor.getPowerConsumption();

        // AC is 10 units, light is 5 units when on
        assertTrue(consumption >= 10);
    }

    @Test
    void equipmentListContainsLightAndAc() {
        SubCorridor corridor = new SubCorridor(1);

        assertEquals(2, corridor.getEquipment().size());
    }
}
