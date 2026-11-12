package com.example.booking.bus.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingRequest(
        @NotNull Long tripId,
        @NotEmpty List<String> seatNumbers,
        @NotNull Long userId
) { }
