package com.example.booking.vendor;

public record VendorResponse(
        Long id,
        Long userId,
        String ownerName,
        String businessName,
        String gstNo,
        VendorType type,
        VendorStatus status
) {}
