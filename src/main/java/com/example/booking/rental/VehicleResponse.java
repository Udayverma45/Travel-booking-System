package com.example.booking.rental;

import java.math.BigDecimal;

public record VehicleResponse(Long id, VehicleType type, String model, String city,
                              BigDecimal pricePerDay, VehicleStatus status) {}
