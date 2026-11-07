package com.example.booking.bus.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripRequest(
        Long busId,
        Long routeId,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        BigDecimal baseFare
) {  }
