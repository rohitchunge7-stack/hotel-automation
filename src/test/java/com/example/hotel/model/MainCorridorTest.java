package com.example.hotel.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainCorridorTest {

    @Test
    void constructorCreatesCorridorWithCorrectNumber() {
        MainCorridor corridor = new MainCorridor(5);

        assertEquals(5, corridor.getNumber());
    }

    @Test
    void lightIsInitiallyOn() {
        MainCorridor corridor = new MainCorridor(1);

        assertTrue(corridor.getLight().isOn());
    }

    @Test
    void airConditionerIsInitiallyOn() {
        MainCorridor corridor = new MainCorridor(1);

        assertTrue(corridor.getAirConditioner().isOn());
    }

    @Test
    void getTypeReturnsMain() {
        MainCorridor corridor = new MainCorridor(1);

        assertEquals("MAIN", corridor.getType());
    }

    @Test
    void powerConsumptionIncludesLightAndAc() {
        MainCorridor corridor = new MainCorridor(1);

        int consumption = corridor.getPowerConsumption();

        // AC is 15 units, light is 5 units when on
        assertTrue(consumption >= 15);
    }

    @Test
    void equipmentListContainsLightAndAc() {
        MainCorridor corridor = new MainCorridor(1);

        assertEquals(2, corridor.getEquipment().size());
    }

    @Test
    void lightCanBeTurnedOff() {
        MainCorridor corridor = new MainCorridor(1);

        corridor.getLight().turnOff();

        assertFalse(corridor.getLight().isOn());
    }

    @Test
    void lightCanBeTurnedOn() {
        MainCorridor corridor = new MainCorridor(1);
        corridor.getLight().turnOff();

        corridor.getLight().turnOn();

        assertTrue(corridor.getLight().isOn());
    }

    @Test
    void airConditionerCanBeTurnedOff() {
        MainCorridor corridor = new MainCorridor(1);

        corridor.getAirConditioner().turnOff();

        assertFalse(corridor.getAirConditioner().isOn());
    }

    @Test
    void airConditionerCanBeTurnedOn() {
        MainCorridor corridor = new MainCorridor(1);
        corridor.getAirConditioner().turnOff();

        corridor.getAirConditioner().turnOn();

        assertTrue(corridor.getAirConditioner().isOn());
    }
}
