package com.example.booking.rental;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record VehicleRequest(
        @NotNull(message = "Type is required") VehicleType type,
        @NotBlank(message = "Model is required") String model,
        @NotBlank(message = "City is required") String city,
        @NotNull(message = "Price is required") @DecimalMin(value = "1.0", message = "Price must be at least 1") BigDecimal pricePerDay
) {}
