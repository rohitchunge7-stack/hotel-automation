package com.example.hotel.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {

    @Test
    void constructorCreatesCorrectNumberOfMainCorridors() {
        Floor floor = new Floor(1, 3, 1);

        assertEquals(3, floor.getMainCorridors().size());
    }

    @Test
    void constructorCreatesCorrectNumberOfSubCorridors() {
        Floor floor = new Floor(1, 1, 4);

        assertEquals(4, floor.getSubCorridors().size());
    }

    @Test
    void constructorSetsCorrectFloorNumber() {
        Floor floor = new Floor(5, 1, 1);

        assertEquals(5, floor.getNumber());
    }

    @Test
    void constructorRejectsZeroFloorNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Floor(0, 1, 1));
    }

    @Test
    void constructorRejectsNegativeFloorNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Floor(-1, 1, 1));
    }

    @Test
    void constructorRejectsZeroMainCorridors() {
        assertThrows(IllegalArgumentException.class, () -> new Floor(1, 0, 1));
    }

    @Test
    void constructorRejectsZeroSubCorridors() {
        assertThrows(IllegalArgumentException.class, () -> new Floor(1, 1, 0));
    }

    @Test
    void powerCapIsCalculatedCorrectly() {
        // Main corridor AC: 15 units, Sub corridor AC: 10 units
        Floor floor = new Floor(1, 2, 3);
        int expectedCap = (2 * 15) + (3 * 10); // 30 + 30 = 60

        assertEquals(expectedCap, floor.getPowerCap());
    }

    @Test
    void powerCapWithSingleCorridors() {
        Floor floor = new Floor(1, 1, 1);
        int expectedCap = 15 + 10; // 25

        assertEquals(expectedCap, floor.getPowerCap());
    }

    @Test
    void getSubCorridorReturnsCorrectCorridor() {
        Floor floor = new Floor(1, 1, 3);

        SubCorridor corridor1 = floor.getSubCorridor(1);
        SubCorridor corridor2 = floor.getSubCorridor(2);
        SubCorridor corridor3 = floor.getSubCorridor(3);

        assertEquals(1, corridor1.getNumber());
        assertEquals(2, corridor2.getNumber());
        assertEquals(3, corridor3.getNumber());
    }

    @Test
    void getSubCorridorRejectsInvalidNumber() {
        Floor floor = new Floor(1, 1, 2);

        assertThrows(IllegalArgumentException.class, () -> floor.getSubCorridor(0));
        assertThrows(IllegalArgumentException.class, () -> floor.getSubCorridor(3));
        assertThrows(IllegalArgumentException.class, () -> floor.getSubCorridor(-1));
    }

    @Test
    void getMainCorridorsReturnsUnmodifiableList() {
        Floor floor = new Floor(1, 1, 1);

        assertThrows(UnsupportedOperationException.class,
                () -> floor.getMainCorridors().add(new MainCorridor(2)));
    }

    @Test
    void getSubCorridorsReturnsUnmodifiableList() {
        Floor floor = new Floor(1, 1, 1);

        assertThrows(UnsupportedOperationException.class,
                () -> floor.getSubCorridors().add(new SubCorridor(2)));
    }

    @Test
    void getActualPowerConsumptionCalculatesCorrectly() {
        Floor floor = new Floor(1, 1, 1);

        // Initial state: main corridor light on, AC on; sub corridor light off, AC on
        int consumption = floor.getActualPowerConsumption();

        assertTrue(consumption > 0);
    }

    @Test
    void corridorsAreNumberedSequentially() {
        Floor floor = new Floor(1, 3, 4);

        for (int i = 0; i < floor.getMainCorridors().size(); i++) {
            assertEquals(i + 1, floor.getMainCorridors().get(i).getNumber());
        }

        for (int i = 0; i < floor.getSubCorridors().size(); i++) {
            assertEquals(i + 1, floor.getSubCorridors().get(i).getNumber());
        }
    }
}
