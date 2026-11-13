package com.example.booking.hotel;

import jakarta.validation.constraints.NotBlank;

public record HotelRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "City is required") String city,
        String address,
        String description
) {}
