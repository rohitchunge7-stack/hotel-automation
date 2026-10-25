package com.example.hotel.policy;

import com.example.hotel.model.SubCorridor;

import java.time.Instant;

public interface CorridorControlPolicy {
    void onMotionDetected(SubCorridor corridor, Instant motionTime);

    void onMotionDetected(SubCorridor corridor, Instant motionTime, boolean nightTime);

    boolean isInactivityTimeoutDue(Instant lastMotionTime, Instant currentTime);

    void onInactivityTimeout(SubCorridor corridor);

    boolean isNightTime(Instant currentTime);
}
