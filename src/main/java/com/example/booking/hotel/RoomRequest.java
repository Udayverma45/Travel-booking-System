package com.example.booking.hotel;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RoomRequest(
        @NotBlank(message = "Room type is required") String roomType,
        @NotNull(message = "Price is required") @DecimalMin(value = "1.0", message = "Price must be at least 1") BigDecimal pricePerNight,
        @Min(value = 1, message = "At least 1 room") int totalRooms
) {}
