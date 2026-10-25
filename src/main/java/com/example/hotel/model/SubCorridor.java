package com.example.hotel.model;

import java.time.Instant;

public final class SubCorridor extends Corridor {
    private static final int AC_POWER_UNITS = 10;
    private Instant lastMotionAt;

    public SubCorridor(int number) {
        super(number, "Sub corridor " + number, AC_POWER_UNITS);
    }

    @Override protected boolean initiallyLightOn() { return false; }
    @Override public String getType() { return "SUB"; }

    public void recordMotion(Instant instant, boolean nightTime) {
        if (instant == null) {
            throw new IllegalArgumentException("Motion time must not be null");
        }
        lastMotionAt = instant;
        if (nightTime) {
            getLight().turnOn();
        } else {
            getLight().turnOff();
        }
        getAirConditioner().turnOn();
    }

    public Instant getLastMotionAt() { return lastMotionAt; }
}
