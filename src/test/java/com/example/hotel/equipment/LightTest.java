package com.example.hotel.equipment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LightTest {

    @Test
    void constructorCreatesLightWithCorrectName() {
        Light light = new Light("Test Light", false);

        assertEquals("Test Light", light.getName());
    }

    @Test
    void constructorCreatesLightInitiallyOn() {
        Light light = new Light("Test Light", true);

        assertTrue(light.isOn());
    }

    @Test
    void constructorCreatesLightInitiallyOff() {
        Light light = new Light("Test Light", false);

        assertFalse(light.isOn());
    }

    @Test
    void constructorRejectsNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Light(null, false));
    }

    @Test
    void constructorRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Light("", false));
    }

    @Test
    void constructorRejectsWhitespaceName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Light("   ", false));
    }

    @Test
    void turnOnTurnsLightOn() {
        Light light = new Light("Test Light", false);

        light.turnOn();

        assertTrue(light.isOn());
    }

    @Test
    void turnOnWhenAlreadyOnRemainsOn() {
        Light light = new Light("Test Light", true);

        light.turnOn();

        assertTrue(light.isOn());
    }

    @Test
    void turnOffTurnsLightOff() {
        Light light = new Light("Test Light", true);

        light.turnOff();

        assertFalse(light.isOn());
    }

    @Test
    void turnOffWhenAlreadyOffRemainsOff() {
        Light light = new Light("Test Light", false);

        light.turnOff();

        assertFalse(light.isOn());
    }

    @Test
    void getPowerConsumptionReturnsFiveWhenOn() {
        Light light = new Light("Test Light", true);

        assertEquals(5, light.getPowerConsumption());
    }

    @Test
    void getPowerConsumptionReturnsZeroWhenOff() {
        Light light = new Light("Test Light", false);

        assertEquals(0, light.getPowerConsumption());
    }

    @Test
    void getPowerConsumptionUpdatesWithState() {
        Light light = new Light("Test Light", false);

        assertEquals(0, light.getPowerConsumption());

        light.turnOn();
        assertEquals(5, light.getPowerConsumption());

        light.turnOff();
        assertEquals(0, light.getPowerConsumption());
    }

    @Test
    void isOnReturnsCorrectState() {
        Light light = new Light("Test Light", false);

        assertFalse(light.isOn());

        light.turnOn();
        assertTrue(light.isOn());

        light.turnOff();
        assertFalse(light.isOn());
    }
}
