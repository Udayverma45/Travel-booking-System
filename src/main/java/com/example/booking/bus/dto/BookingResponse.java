package com.example.booking.bus.dto;

import com.example.booking.booking.entity.BookingStatus;
import com.example.booking.booking.entity.BookingType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id,
        BookingType type,
        BookingStatus status,
        BigDecimal totalAmount,
        List<String> seatNumbers,
        LocalDateTime createdAt
) {
}
