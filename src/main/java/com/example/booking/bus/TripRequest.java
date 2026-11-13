package com.example.booking.bus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripRequest(
        @NotNull(message = "busId is required") Long busId,
        @NotNull(message = "routeId is required") Long routeId,
        @NotNull(message = "departureTime is required") LocalDateTime departureTime,
        @NotNull(message = "arrivalTime is required") LocalDateTime arrivalTime,
        @NotNull(message = "baseFare is required")
        @DecimalMin(value = "1.0", message = "Fare must be at least 1") BigDecimal baseFare
) {}
