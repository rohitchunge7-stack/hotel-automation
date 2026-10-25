package com.example.hotel.app;

import com.example.hotel.config.HotelControllerConfiguration;
import com.example.hotel.controller.HotelController;
import com.example.hotel.model.Hotel;
import com.example.hotel.policy.DefaultCorridorControlPolicy;
import com.example.hotel.policy.DefaultPowerManagementPolicy;

import java.time.Clock;
import java.time.LocalTime;
import java.util.Scanner;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            int floors = readPositiveInt(scanner, "Number of floors: ");
            int mainCorridors = readPositiveInt(scanner, "Main corridors per floor: ");
            int subCorridors = readPositiveInt(scanner, "Sub corridors per floor: ");

            Hotel hotel = new Hotel(floors, mainCorridors, subCorridors);
            HotelControllerConfiguration configuration = HotelControllerConfiguration.load();
            HotelController controller = new HotelController(
                    hotel,
                    new DefaultPowerManagementPolicy(),
                    new DefaultCorridorControlPolicy(configuration),
                    Clock.systemDefaultZone());

            System.out.println("\nInitial state (power policy applied):");
            printClockDecision(controller, "Startup: main-corridor lighting is initialized from the actual local time.");
            ConsoleStatePrinter.print(hotel);
            printCommands();

            while (true) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) break;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                if ("EXIT".equalsIgnoreCase(line)) break;

                try {
                    String explanation = processCommand(line, controller);
                    System.out.println("\n[Decision] " + explanation);
                    if (!"EXIT".equalsIgnoreCase(line)) {
                        ConsoleStatePrinter.print(hotel);
                    }
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    System.out.println("Could not process event: " + ex.getMessage());
                }
            }
        } catch (IllegalArgumentException ex) {
            System.err.println("Invalid configuration: " + ex.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                int parsed = Integer.parseInt(value);
                if (parsed > 0) return parsed;
            } catch (NumberFormatException ignored) {
                throw new NumberFormatException("Can't read positive integer");
                // Report a clear input message below.
            }
            System.out.println("Please enter a positive whole number.");
        }
    }

    private static String processCommand(String line, HotelController controller) {
        String[] parts = line.split("\\s+");
        if ("PRINT".equalsIgnoreCase(parts[0]) && parts.length == 1) {
            controller.refreshTimeBasedLighting();
            return "PRINT refreshed main-corridor lights using the actual local time. "
                    + clockDecision(controller);
        }
        if ("MOTION".equalsIgnoreCase(parts[0])) {
            if (parts.length != 3 && parts.length != 4) {
                throw new IllegalArgumentException("Use MOTION floor subCorridor [DAY|NIGHT]");
            }
            int floor = parsePositive(parts[1], "floor");
            int subCorridor = parsePositive(parts[2], "sub corridor");
            Boolean nightOverride = null;
            if (parts.length == 4) {
                if ("NIGHT".equalsIgnoreCase(parts[3])) {
                    nightOverride = Boolean.TRUE;
                } else if ("DAY".equalsIgnoreCase(parts[3])) {
                    nightOverride = Boolean.FALSE;
                } else {
                    throw new IllegalArgumentException("Optional MOTION value must be DAY or NIGHT");
                }
            }
            controller.motionDetected(floor, subCorridor, nightOverride);
            if (nightOverride != null) {
                String mode = nightOverride.booleanValue() ? "NIGHT" : "DAY";
                return "MOTION recorded for floor " + floor + ", sub-corridor " + subCorridor
                        + ". Manual " + mode + " override was used, so the clock check was skipped for this motion command. "
                        + (nightOverride.booleanValue()
                            ? "Night behavior: main-corridor lights are ON and the target sub-corridor light is requested ON."
                            : "Day behavior: main-corridor lights are OFF and the target sub-corridor light is kept OFF.")
                        + " The power policy was then applied.";
            }
            return "MOTION recorded for floor " + floor + ", sub-corridor " + subCorridor + ". "
                    + clockDecision(controller) + " The target sub-corridor light follows that actual-time decision. "
                    + "The power policy was then applied.";
        }

        if (parts.length != 3) {
            throw new IllegalArgumentException("Use MOTION floor subCorridor [DAY|NIGHT], TIMEOUT floor subCorridor, INACTIVE floor subCorridor, PRINT or EXIT");
        }
        int floor = parsePositive(parts[1], "floor");
        int subCorridor = parsePositive(parts[2], "sub corridor");

        if ("TIMEOUT".equalsIgnoreCase(parts[0])) {
            controller.inactivityTimeout(floor, subCorridor);
            return "TIMEOUT applied to floor " + floor + ", sub-corridor " + subCorridor
                    + ". More than one minute has elapsed since motion; the light was requested OFF and the AC requested ON. "
                    + "The power policy was then applied. " + clockDecision(controller);
        } else if ("INACTIVE".equalsIgnoreCase(parts[0])) {
            controller.simulateInactivityTimeout(floor, subCorridor);
            return "INACTIVE testing shortcut applied to floor " + floor + ", sub-corridor " + subCorridor
                    + " without waiting. The light was requested OFF and the AC requested ON. "
                    + "The power policy was then applied. " + clockDecision(controller);
        } else {
            throw new IllegalArgumentException("Unknown command: " + parts[0]);
        }
    }

    private static void printClockDecision(HotelController controller, String heading) {
        System.out.println(heading + " " + clockDecision(controller));
    }

    private static String clockDecision(HotelController controller) {
        LocalTime time = controller.getCurrentLocalTime();
        boolean night = controller.isNightTimeNow();
        return String.format("Actual local time: %s (%s). Night-time window is 18:00 inclusive to 06:00 exclusive. "
                        + "Clock decision: %s; main-corridor lights are %s.",
                time.withNano(0), controller.getTimeZone(), night ? "NIGHT" : "DAY", night ? "ON" : "OFF");
    }

    private static int parsePositive(String value, String label) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed > 0) return parsed;
        } catch (NumberFormatException e) {

        }
        throw new IllegalArgumentException(label + " must be a positive whole number");
    }

    private static void printCommands() {
        System.out.println("Commands:");
        System.out.println("  MOTION <floor> <subCorridor> [DAY|NIGHT]");
        System.out.println("       With DAY/NIGHT, the clock check is skipped for that MOTION command only.");
        System.out.println("       Without it, the actual local time decides: NIGHT = 18:00-05:59; DAY = 06:00-17:59.");
        System.out.println("  TIMEOUT <floor> <subCorridor>  (only after > 1 minute inactivity)");
        System.out.println("  INACTIVE <floor> <subCorridor> (testing shortcut; no waiting)");
        System.out.println("  PRINT");
        System.out.println("  EXIT");
    }
}
