package com.example.booking.vendor;

import java.math.BigDecimal;

public record VendorDashboardResponse(
        long totalBookings,
        long confirmedBookings,
        long cancelledBookings,
        BigDecimal revenue
) {}
