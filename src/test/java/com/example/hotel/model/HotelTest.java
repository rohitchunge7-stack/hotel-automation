package com.example.hotel.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HotelTest {

    @Test
    void constructorCreatesCorrectNumberOfFloors() {
        Hotel hotel = new Hotel(2, 1, 1);

        assertEquals(2, hotel.getFloors().size());
    }

    @Test
    void constructorCreatesFloorsWithCorrectConfiguration() {
        Hotel hotel = new Hotel(1, 2, 3);

        Floor floor = hotel.getFloor(1);
        assertEquals(2, floor.getMainCorridors().size());
        assertEquals(3, floor.getSubCorridors().size());
    }

    @Test
    void constructorRejectsZeroFloors() {
        assertThrows(IllegalArgumentException.class, () -> new Hotel(0, 1, 1));
    }

    @Test
    void constructorRejectsNegativeFloors() {
        assertThrows(IllegalArgumentException.class, () -> new Hotel(-1, 1, 1));
    }

    @Test
    void constructorRejectsZeroMainCorridors() {
        assertThrows(IllegalArgumentException.class, () -> new Hotel(1, 0, 1));
    }

    @Test
    void constructorRejectsZeroSubCorridors() {
        assertThrows(IllegalArgumentException.class, () -> new Hotel(1, 1, 0));
    }

    @Test
    void getFloorReturnsCorrectFloor() {
        Hotel hotel = new Hotel(3, 1, 1);

        Floor floor1 = hotel.getFloor(1);
        Floor floor2 = hotel.getFloor(2);
        Floor floor3 = hotel.getFloor(3);

        assertEquals(1, floor1.getNumber());
        assertEquals(2, floor2.getNumber());
        assertEquals(3, floor3.getNumber());
    }

    @Test
    void getFloorRejectsInvalidFloorNumber() {
        Hotel hotel = new Hotel(2, 1, 1);

        assertThrows(IllegalArgumentException.class, () -> hotel.getFloor(0));
        assertThrows(IllegalArgumentException.class, () -> hotel.getFloor(3));
        assertThrows(IllegalArgumentException.class, () -> hotel.getFloor(-1));
    }

    @Test
    void getFloorsReturnsUnmodifiableList() {
        Hotel hotel = new Hotel(1, 1, 1);

        assertThrows(UnsupportedOperationException.class,
                () -> hotel.getFloors().add(new Floor(2, 1, 1)));
    }

    @Test
    void floorsAreNumberedSequentially() {
        Hotel hotel = new Hotel(5, 1, 1);

        for (int i = 0; i < hotel.getFloors().size(); i++) {
            assertEquals(i + 1, hotel.getFloors().get(i).getNumber());
        }
    }
}
