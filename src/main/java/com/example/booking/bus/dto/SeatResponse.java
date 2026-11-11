package com.example.booking.bus.dto;

import com.example.booking.bus.entity.SeatStatus;

public record SeatResponse (
        Long id,
        String seatNumber,
        SeatStatus status
)
{

}

