package com.example.booking.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record HotelBookingRequest(
        @NotNull(message = "roomId is required") Long roomId,
        @NotNull(message = "checkIn is required") LocalDate checkIn,
        @NotNull(message = "checkOut is required") LocalDate checkOut,
        @Min(value = 1, message = "At least 1 room") int rooms
) {}
