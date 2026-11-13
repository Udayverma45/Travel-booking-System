package com.example.booking.booking;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RentalBookingRequest(
        @NotNull(message = "vehicleId is required") Long vehicleId,
        @NotNull(message = "startDate is required") LocalDate startDate,
        @NotNull(message = "endDate is required") LocalDate endDate
) {}
