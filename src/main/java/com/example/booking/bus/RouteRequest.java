package com.example.booking.bus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RouteRequest(
        @NotBlank(message = "Source is required") String source,
        @NotBlank(message = "Destination is required") String destination,
        @Min(value = 1, message = "Distance must be at least 1 km") int distanceKm
) {}
