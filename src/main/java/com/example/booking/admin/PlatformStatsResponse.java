package com.example.booking.admin;

import java.math.BigDecimal;

public record PlatformStatsResponse(
        long totalUsers,
        long totalVendors,
        long pendingVendors,
        long totalBookings,
        long confirmedBookings,
        BigDecimal totalRevenue
) {}
