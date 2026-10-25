package com.example.hotel.model;

public final class MainCorridor extends Corridor {
    private static final int AC_POWER_UNITS = 15;

    public MainCorridor(int number) {
        super(number, "Main corridor " + number, AC_POWER_UNITS);
    }

    @Override protected boolean initiallyLightOn() { return true; }
    @Override public String getType() { return "MAIN"; }
}
