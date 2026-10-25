package com.example.hotel.policy;

import com.example.hotel.model.Floor;
import com.example.hotel.model.SubCorridor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class DefaultPowerManagementPolicy implements PowerManagementPolicy {
    @Override
    public void enforceLimit(Floor floor) {
        if (floor == null) throw new IllegalArgumentException("Floor must not be null");


        List<SubCorridor> candidates = new ArrayList<SubCorridor>(floor.getSubCorridors());
        candidates.sort(Comparator.comparing(SubCorridor::getLastMotionAt,
                Comparator.nullsFirst(Comparator.<Instant>naturalOrder())));

        for (SubCorridor corridor : candidates) {
            if (floor.getActualPowerConsumption() <= floor.getPowerCap()) return;
            if (corridor.getAirConditioner().isOn()) {
                corridor.getAirConditioner().turnOff();
            }
        }

        if (floor.getActualPowerConsumption() > floor.getPowerCap()) {
            throw new IllegalStateException("Floor " + floor.getNumber()
                    + " cannot meet power cap " + floor.getPowerCap()
                    + "; actual consumption is " + floor.getActualPowerConsumption());
        }
    }
}
