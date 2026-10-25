package com.example.hotel.equipment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirConditionerTest {

    @Test
    void constructorCreatesAcWithCorrectName() {
        AirConditioner ac = new AirConditioner("Test AC", 10, false);

        assertEquals("Test AC", ac.getName());
    }

    @Test
    void constructorCreatesAcWithCorrectPowerUnits() {
        AirConditioner ac = new AirConditioner("Test AC", 15, false);

        assertEquals(15, ac.getPowerConsumption());
    }

    @Test
    void constructorCreatesAcInitiallyOn() {
        AirConditioner ac = new AirConditioner("Test AC", 10, true);

        assertTrue(ac.isOn());
    }

    @Test
    void constructorCreatesAcInitiallyOff() {
        AirConditioner ac = new AirConditioner("Test AC", 10, false);

        assertFalse(ac.isOn());
    }

    @Test
    void constructorRejectsNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new AirConditioner(null, 10, false));
    }

    @Test
    void constructorRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> new AirConditioner("", 10, false));
    }

    @Test
    void constructorRejectsWhitespaceName() {
        assertThrows(IllegalArgumentException.class,
                () -> new AirConditioner("   ", 10, false));
    }

    @Test
    void constructorRejectsZeroPowerUnits() {
        assertThrows(IllegalArgumentException.class,
                () -> new AirConditioner("Test AC", 0, false));
    }

    @Test
    void constructorRejectsNegativePowerUnits() {
        assertThrows(IllegalArgumentException.class,
                () -> new AirConditioner("Test AC", -5, false));
    }

    @Test
    void turnOnTurnsAcOn() {
        AirConditioner ac = new AirConditioner("Test AC", 10, false);

        ac.turnOn();

        assertTrue(ac.isOn());
    }

    @Test
    void turnOnWhenAlreadyOnRemainsOn() {
        AirConditioner ac = new AirConditioner("Test AC", 10, true);

        ac.turnOn();

        assertTrue(ac.isOn());
    }

    @Test
    void turnOffTurnsAcOff() {
        AirConditioner ac = new AirConditioner("Test AC", 10, true);

        ac.turnOff();

        assertFalse(ac.isOn());
    }

    @Test
    void turnOffWhenAlreadyOffRemainsOff() {
        AirConditioner ac = new AirConditioner("Test AC", 10, false);

        ac.turnOff();

        assertFalse(ac.isOn());
    }

    @Test
    void getPowerConsumptionReturnsPowerUnitsWhenOn() {
        AirConditioner ac = new AirConditioner("Test AC", 15, true);

        assertEquals(15, ac.getPowerConsumption());
    }

    @Test
    void getPowerConsumptionReturnsZeroWhenOff() {
        AirConditioner ac = new AirConditioner("Test AC", 15, false);

        assertEquals(0, ac.getPowerConsumption());
    }

    @Test
    void getPowerConsumptionUpdatesWithState() {
        AirConditioner ac = new AirConditioner("Test AC", 10, false);

        assertEquals(0, ac.getPowerConsumption());

        ac.turnOn();
        assertEquals(10, ac.getPowerConsumption());

        ac.turnOff();
        assertEquals(0, ac.getPowerConsumption());
    }

    @Test
    void isOnReturnsCorrectState() {
        AirConditioner ac = new AirConditioner("Test AC", 10, false);

        assertFalse(ac.isOn());

        ac.turnOn();
        assertTrue(ac.isOn());

        ac.turnOff();
        assertFalse(ac.isOn());
    }

    @Test
    void canCreateAcWithDifferentPowerUnits() {
        AirConditioner ac1 = new AirConditioner("AC1", 5, true);
        AirConditioner ac2 = new AirConditioner("AC2", 10, true);
        AirConditioner ac3 = new AirConditioner("AC3", 20, true);

        assertEquals(5, ac1.getPowerConsumption());
        assertEquals(10, ac2.getPowerConsumption());
        assertEquals(20, ac3.getPowerConsumption());
    }
}
