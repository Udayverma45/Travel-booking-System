package com.example.booking.booking;

import java.util.List;

public enum BookingStatus {
    PENDING, CONFIRMED, CANCELLED, EXPIRED;

    /** Inventory block karne wali bookings (availability check me ginti hain) */
    public static final List<BookingStatus> ACTIVE = List.of(PENDING, CONFIRMED);
}
