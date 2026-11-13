package com.example.booking.payment;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(@NotNull(message = "bookingId is required") Long bookingId) {}
