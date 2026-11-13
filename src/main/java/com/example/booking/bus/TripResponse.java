package com.example.booking.bus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripResponse(
        Long id,
        String busNumber,
        BusType busType,
        String source,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        BigDecimal baseFare,
        TripStatus status,
        long availableSeats
) {}
