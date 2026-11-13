package com.example.booking.payment;

import java.math.BigDecimal;

public record CreateOrderResponse(Long bookingId, String orderId, BigDecimal amount, String currency) {}
