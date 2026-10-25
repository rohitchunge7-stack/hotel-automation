# Hotel Electricity Controller

A Java application that manages electricity consumption in hotel corridors through automated light and AC control based on motion detection and time of day.

## Running the Application

Requirements: JDK 8+ and Maven.

```bash
mvn test
mvn package
java -cp target/classes com.example.hotel.app.Main
```

For detailed usage instructions, see **USER_MANUAL.md**.

## Available Commands

- `MOTION <floorNumber> <subCorridorNumber>` - Detects motion and uses actual local time (Night: 6:00 PM to 6:00 AM)
- `MOTION <floorNumber> <subCorridorNumber> NIGHT` - Override to treat as night time
- `MOTION <floorNumber> <subCorridorNumber> DAY` - Override to treat as day time
- `TIMEOUT <floorNumber> <subCorridorNumber>` - Handle inactivity (only after >1 minute since last motion)
- `INACTIVE <floorNumber> <subCorridorNumber>` - Testing shortcut to apply inactivity immediately
- `PRINT` - Refresh main corridor lights based on current time and display state
- `EXIT` - Exit the application

The console displays the current time, time zone, day/night decision, and explains each command's behavior before showing the updated state.

## Power Calculation

Each floor has a power cap calculated as: `mainCorridors * 15 + subCorridors * 10`

Power consumption:
- Light: 5 units when ON
- Main corridor AC: 15 units when ON
- Sub corridor AC: 10 units when ON

When power exceeds the cap, the system turns off sub-corridor ACs starting with corridors that haven't had recent motion.

## Night Time Rules

Night time is from 6:00 PM to 6:00 AM (18:00-06:00).

- Main corridor lights: ON at night, OFF during day
- Sub corridor lights: Turn ON only after motion during night time
- During day time, motion is recorded but sub corridor lights stay OFF
- Power cap is enforced after every event

## Architecture

The application uses a flexible, policy-based design:

- `Equipment` interface for all controllable devices
- `PowerManagementPolicy` for power limit enforcement
- `CorridorControlPolicy` for motion and inactivity handling
- `HotelControllerConfiguration` for loading settings from properties file
- Inactivity threshold defaults to 60 seconds (configurable in hotel-controller.properties)
