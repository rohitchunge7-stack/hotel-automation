# User Manual — Hotel Electricity Controller

This manual explains how to start the console application, enter configuration, test day/night behavior, test inactivity, and understand each status message.

## 1. Start the application

Open the project in IntelliJ IDEA and run `com.example.hotel.app.Main`. Alternatively, from the project directory run:

```bash
mvn test
mvn package
java -cp target/classes com.example.hotel.app.Main
```

The application asks for three positive whole numbers:

1. `Number of floors`
2. `Main corridors per floor`
3. `Sub corridors per floor`

For the examples below, enter:

```text
Number of floors: 2
Main corridors per floor: 1
Sub corridors per floor: 2
```

The app prints the initial state, the actual local time, the time zone, and the clock-based DAY/NIGHT decision. Night is **6:00 PM inclusive until 6:00 AM exclusive**.

## 2. Commands and inputs

Enter one command at the `>` prompt and press Enter. Use floor and sub-corridor numbers that exist in your configuration.

| Input | Meaning |
|---|---|
| `MOTION 1 2` | Record motion in floor 1, sub-corridor 2. The app checks the actual local time. |
| `MOTION 1 2 NIGHT` | Testing override: treat this motion as night-time and skip the clock check for this motion command. |
| `MOTION 1 2 DAY` | Testing override: treat this motion as daytime and skip the clock check for this motion command. |
| `INACTIVE 1 2` | Testing shortcut: immediately request the target light OFF and AC ON; does not wait for one minute. Requires a previous motion for that corridor. |
| `TIMEOUT 1 2` | Real timeout: succeeds only after more than 60 seconds since the last motion. |
| `PRINT` | Refresh main-corridor lights from the actual local time and print the current state. |
| `EXIT` | Stop the application. |

The optional `DAY` or `NIGHT` applies only to that single `MOTION` command. It does not permanently change the system clock or the behavior of later commands. A command without the override always uses actual local time.

## 3. What the output means

After each successful command, the app prints a `[Decision]` explanation followed by the state of every floor and corridor. The explanation includes:

- the command that was applied and which corridor it targeted;
- whether the command used the real clock or a manual DAY/NIGHT override;
- for clock-based decisions, the actual local time, time zone, DAY/NIGHT result, and main-corridor light decision;
- whether the power policy was applied.

Each floor line looks like `Floor 1 | power=30/35 units`. The first number is current combined consumption; the second is the floor's power cap. Each corridor line shows `Light=ON/OFF` and `AC=ON/OFF`. ON equipment consumes power. To keep a floor within its cap, the power policy may switch a sub-corridor AC OFF even when an event requested it ON. This is expected policy behavior, not necessarily an error.

## 4. Recommended test scenarios

Use the example configuration above. Run each command separately and read the `[Decision]` message and state after each step.

### Test A — Force night-time without waiting for evening

Input:

```text
MOTION 1 2 NIGHT
```

Expected explanation: manual NIGHT override used; clock check skipped for this motion command. Main-corridor lights are set ON, and sub-corridor 2's light is requested ON. Power management then enforces the floor cap.

### Test B — Force daytime even if the real time is night

Input:

```text
MOTION 1 2 DAY
```

Expected explanation: manual DAY override used; clock check skipped for this motion command. Main-corridor lights are set OFF, and sub-corridor 2's light remains OFF. Motion is still recorded, and the power policy runs.

### Test C — Verify actual-time behavior

Input:

```text
MOTION 1 2
```

Read the printed actual local time and clock decision:

- From 06:00:00 through 17:59:59: decision is DAY; main-corridor lights are OFF and the target sub-corridor light stays OFF after motion.
- From 18:00:00 through 23:59:59, and from 00:00:00 through 05:59:59: decision is NIGHT; main-corridor lights are ON and the target sub-corridor light is requested ON after motion.

### Test D — Test inactivity immediately

First record motion, then enter:

```text
MOTION 1 2 NIGHT
INACTIVE 1 2
```

Expected explanation for `INACTIVE`: testing shortcut used, no waiting required; the target light is requested OFF and its AC requested ON. The power policy then enforces the cap and may switch a sub-corridor AC back OFF if needed.

### Test E — Test the real one-minute timeout

Input:

```text
MOTION 1 2 NIGHT
```

Wait for **more than 60 seconds**, then enter:

```text
TIMEOUT 1 2
```

Expected result: timeout applies, light is requested OFF, AC requested ON, and the power policy is enforced. If you try too early, the app explains that the inactivity threshold has not yet been exceeded.

### Test F — Refresh the actual-time main-corridor lighting

Input:

```text
PRINT
```

The app refreshes main-corridor lights based on the actual local time and prints the clock decision and current state. `PRINT` does not use a previous command's DAY/NIGHT override.

## 5. Important notes

- Use `NIGHT` and `DAY` to test the two cases immediately; you do not need to change your computer's clock.
- Use `MOTION 1 2` without an override to verify the actual clock-based decision.
- The power cap is checked after each event. Equipment states may be adjusted by the power policy to satisfy the assignment's consumption limit.
- Invalid commands show a helpful error message; use the command formats above.
- The manual override is for testing only. Normal commands continue to use the real local time.
