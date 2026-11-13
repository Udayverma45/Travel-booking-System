package com.example.booking.payment;

import jakarta.validation.constraints.NotBlank;

/**
 * Mock gateway: success=true bhejo to payment pass, false to fail.
 * Razorpay me yahan razorpay_payment_id aur razorpay_signature aayenge.
 */
public record VerifyPaymentRequest(
        @NotBlank(message = "orderId is required") String orderId,
        String paymentId,
        boolean success
) {}
