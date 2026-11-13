package com.example.booking.auth;

import com.example.booking.user.Role;
import com.example.booking.vendor.VendorType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name is required") String name,
        @Email(message = "Valid email required") @NotBlank(message = "Email is required") String email,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
        String phone,
        Role role,                 // CUSTOMER (default) ya VENDOR
        String businessName,       // sirf VENDOR ke liye
        String gstNo,              // sirf VENDOR ke liye
        VendorType vendorType      // sirf VENDOR ke liye: BUS / HOTEL / RENTAL
) {}
