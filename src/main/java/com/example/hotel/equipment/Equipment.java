package com.example.hotel.equipment;

public interface Equipment {
    void turnOn();
    void turnOff();
    boolean isOn();
    int getPowerConsumption();
    String getName();
}
