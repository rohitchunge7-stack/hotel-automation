package com.example.hotel.equipment;

public final class AirConditioner implements Equipment {
    private final String name;
    private final int powerUnits;
    private boolean on;

    public AirConditioner(String name, int powerUnits, boolean initiallyOn) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("AC name must not be blank");
        }
        if (powerUnits <= 0) {
            throw new IllegalArgumentException("AC power must be positive");
        }
        this.name = name;
        this.powerUnits = powerUnits;
        this.on = initiallyOn;
    }

    @Override public void turnOn() { on = true; }
    @Override public void turnOff() { on = false; }
    @Override public boolean isOn() { return on; }
    @Override public int getPowerConsumption() { return on ? powerUnits : 0; }
    @Override public String getName() { return name; }
}
