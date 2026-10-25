package com.example.hotel.model;

import com.example.hotel.equipment.AirConditioner;
import com.example.hotel.equipment.Equipment;
import com.example.hotel.equipment.Light;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Corridor {
    private final int number;
    private final Light light;
    private final AirConditioner airConditioner;

    protected Corridor(int number, String label, int acPowerUnits) {
        if (number < 1) {
            throw new IllegalArgumentException("Corridor number must be >= 1");
        }
        this.number = number;
        this.light = new Light(label + " light", initiallyLightOn());
        this.airConditioner = new AirConditioner(label + " AC", acPowerUnits, true);
    }

    protected abstract boolean initiallyLightOn();

    public int getNumber() { return number; }
    public Light getLight() { return light; }
    public AirConditioner getAirConditioner() { return airConditioner; }

    public int getPowerConsumption() {
        return light.getPowerConsumption() + airConditioner.getPowerConsumption();
    }

    public List<Equipment> getEquipment() {
        return Collections.unmodifiableList(Arrays.<Equipment>asList(light, airConditioner));
    }

    public abstract String getType();
}
