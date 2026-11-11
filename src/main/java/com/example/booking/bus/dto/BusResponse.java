package com.example.booking.bus.dto;

import com.example.booking.bus.entity.BusType;

public record BusResponse(
        Long id,
        String busNumbber,
        BusType busType,
        int totalSrats
){

}
