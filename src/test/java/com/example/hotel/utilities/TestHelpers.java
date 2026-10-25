package com.example.hotel.utilities;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public final class TestHelpers {
    private TestHelpers() { }

    public static Clock fixedClock(String instant) {
        return Clock.fixed(Instant.parse(instant), ZoneOffset.UTC);
    }

    public static Instant atLocalTime(String time) {
        return LocalDate.of(2026, 1, 1)
                .atTime(LocalTime.parse(time))
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    public static Instant atLocalTime(String time, ZoneId zoneId) {
        return LocalDate.of(2026, 1, 1)
                .atTime(LocalTime.parse(time))
                .atZone(zoneId)
                .toInstant();
    }
}
