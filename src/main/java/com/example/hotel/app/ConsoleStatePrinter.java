package com.example.hotel.app;

import com.example.hotel.model.Corridor;
import com.example.hotel.model.Floor;
import com.example.hotel.model.Hotel;
import com.example.hotel.model.MainCorridor;
import com.example.hotel.model.SubCorridor;

public final class ConsoleStatePrinter {
    private ConsoleStatePrinter() { }

    public static void print(Hotel hotel) {
        for (Floor floor : hotel.getFloors()) {
            System.out.println("Floor " + floor.getNumber()
                    + " | power=" + floor.getActualPowerConsumption()
                    + "/" + floor.getPowerCap() + " units");
            for (MainCorridor corridor : floor.getMainCorridors()) {
                printCorridor("  Main corridor " + corridor.getNumber(), corridor);
            }
            for (SubCorridor corridor : floor.getSubCorridors()) {
                printCorridor("  Sub corridor " + corridor.getNumber(), corridor);
            }
        }
        System.out.println("Note: ON equipment consumes power. The power policy may switch sub-corridor ACs OFF to keep each floor within its power cap.");
        System.out.println();
    }

    private static void printCorridor(String label, Corridor corridor) {
        System.out.println(label + " | Light=" + state(corridor.getLight().isOn())
                + " | AC=" + state(corridor.getAirConditioner().isOn()));
    }

    private static String state(boolean on) { return on ? "ON" : "OFF"; }
}
