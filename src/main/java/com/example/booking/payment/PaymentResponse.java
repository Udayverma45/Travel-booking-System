package com.example.booking.payment;

import com.example.booking.booking.BookingStatus;

import java.math.BigDecimal;

public record PaymentResponse(Long bookingId, String orderId, PaymentStatus paymentStatus,
                              BookingStatus bookingStatus, BigDecimal amount) {}
