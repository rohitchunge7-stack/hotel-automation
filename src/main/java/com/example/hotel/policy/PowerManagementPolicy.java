package com.example.hotel.policy;

import com.example.hotel.model.Floor;

public interface PowerManagementPolicy {
    void enforceLimit(Floor floor);
}
