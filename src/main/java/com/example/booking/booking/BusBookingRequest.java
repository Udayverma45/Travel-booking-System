package com.example.booking.booking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BusBookingRequest(
        @NotNull(message = "tripId is required") Long tripId,
        @NotEmpty(message = "Select at least one seat") List<String> seatNumbers
) {}
