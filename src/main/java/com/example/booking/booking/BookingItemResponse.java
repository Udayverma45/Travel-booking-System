package com.example.booking.booking;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingItemResponse(String description, int quantity, BigDecimal price,
                                  LocalDate startDate, LocalDate endDate) {}
