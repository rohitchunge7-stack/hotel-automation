package com.example.hotel.utilities;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * A mutable clock for testing purposes that allows controlling time during tests.
 */
public final class TestClock extends Clock {
    private Instant instant;

    public TestClock(Instant instant) {
        this.instant = instant;
    }

    public TestClock(String instant) {
        this.instant = Instant.parse(instant);
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public void setInstant(String instant) {
        this.instant = Instant.parse(instant);
    }

    public void advanceSeconds(long seconds) {
        this.instant = this.instant.plusSeconds(seconds);
    }

    @Override
    public ZoneOffset getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(java.time.ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return instant;
    }
}
