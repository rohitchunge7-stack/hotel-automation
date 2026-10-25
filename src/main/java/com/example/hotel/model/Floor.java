package com.example.hotel.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Floor {
    private final int number;
    private final List<MainCorridor> mainCorridors;
    private final List<SubCorridor> subCorridors;
    private final int powerCap;

    public Floor(int number, int mainCorridorCount, int subCorridorCount) {
        if (number < 1) throw new IllegalArgumentException("Floor number must be >= 1");
        if (mainCorridorCount < 1) throw new IllegalArgumentException("Each floor needs at least one main corridor");
        if (subCorridorCount < 1) throw new IllegalArgumentException("Each floor needs at least one sub corridor");

        this.number = number;
        this.mainCorridors = new ArrayList<MainCorridor>();
        this.subCorridors = new ArrayList<SubCorridor>();

        for (int i = 1; i <= mainCorridorCount; i++) {
            mainCorridors.add(new MainCorridor(i));
        }
        for (int i = 1; i <= subCorridorCount; i++) {
            subCorridors.add(new SubCorridor(i));
        }

        // As specified by the assignment: AC capacity, not including lights.
        this.powerCap = (mainCorridorCount * 15) + (subCorridorCount * 10);
    }

    public int getNumber() { return number; }
    public int getPowerCap() { return powerCap; }
    public List<MainCorridor> getMainCorridors() { return Collections.unmodifiableList(mainCorridors); }
    public List<SubCorridor> getSubCorridors() { return Collections.unmodifiableList(subCorridors); }

    public SubCorridor getSubCorridor(int corridorNumber) {
        if (corridorNumber < 1 || corridorNumber > subCorridors.size()) {
            throw new IllegalArgumentException("Invalid sub corridor number " + corridorNumber
                    + " on floor " + number);
        }
        return subCorridors.get(corridorNumber - 1);
    }

    public int getActualPowerConsumption() {
        int total = 0;
        for (MainCorridor corridor : mainCorridors) total += corridor.getPowerConsumption();
        for (SubCorridor corridor : subCorridors) total += corridor.getPowerConsumption();
        return total;
    }
}
