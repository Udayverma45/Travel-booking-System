package com.example.booking.bus.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SeatLockRequest (

        @NotEmpty(message = "Select at least one seat")
        List<String> seatNumbers,

        @NotNull(message = "user id is required")
        Long userId
) { }
