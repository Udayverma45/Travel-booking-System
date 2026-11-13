package com.example.booking.bus;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SeatLockRequest(
        @NotEmpty(message = "Select at least one seat") List<String> seatNumbers
) {}
