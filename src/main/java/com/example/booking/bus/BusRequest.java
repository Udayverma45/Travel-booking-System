package com.example.booking.bus;

import jakarta.validation.constraints.*;

public record BusRequest(
        @NotBlank(message = "Bus number is required")
        String busNumber,

        @NotNull(message = "Bus type is required")
        BusType busType,

        @Min(value = 1, message = "Total seats must be at least 1")
        @Max(value = 100, message = "Total seats cannot exceed 100")
        int totalSeats
) {}
