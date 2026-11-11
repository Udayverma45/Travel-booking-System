package com.example.booking.bus.dto;

import com.example.booking.bus.entity.BusType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BusRequest (

    @NotBlank(message = "Bus number id required")
    String busNumber,

    @NotNull(message = "Bus type is required")
    BusType busType,

    @Min(value = 1 , message = "Total seats must be at least 1")
    @Max(value = 100 , message = "Total seat  can not be exceed 100")
    int totalSeats
) {}
