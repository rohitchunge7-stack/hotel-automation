package com.example.hotel.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Hotel {
    private final List<Floor> floors;

    public Hotel(int floorCount, int mainCorridorsPerFloor, int subCorridorsPerFloor) {
        if (floorCount < 1) throw new IllegalArgumentException("Hotel must have at least one floor");
        if (mainCorridorsPerFloor < 1) throw new IllegalArgumentException("Main corridor count must be >= 1");
        if (subCorridorsPerFloor < 1) throw new IllegalArgumentException("Sub corridor count must be >= 1");

        floors = new ArrayList<Floor>();
        for (int i = 1; i <= floorCount; i++) {
            floors.add(new Floor(i, mainCorridorsPerFloor, subCorridorsPerFloor));
        }
    }

    public List<Floor> getFloors() { return Collections.unmodifiableList(floors); }

    public Floor getFloor(int floorNumber) {
        if (floorNumber < 1 || floorNumber > floors.size()) {
            throw new IllegalArgumentException("Invalid floor number " + floorNumber);
        }
        return floors.get(floorNumber - 1);
    }
}
