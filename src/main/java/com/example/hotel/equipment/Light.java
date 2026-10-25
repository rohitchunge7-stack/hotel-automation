package com.example.hotel.equipment;

public final class Light implements Equipment {
    private static final int POWER_UNITS = 5;
    private final String name;
    private boolean on;

    public Light(String name, boolean initiallyOn) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Light name must not be blank");
        }
        this.name = name;
        this.on = initiallyOn;
    }

    @Override public void turnOn() { on = true; }
    @Override public void turnOff() { on = false; }
    @Override public boolean isOn() { return on; }
    @Override public int getPowerConsumption() { return on ? POWER_UNITS : 0; }
    @Override public String getName() { return name; }
}
