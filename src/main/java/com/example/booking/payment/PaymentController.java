package com.example.booking.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return paymentService.createOrder(request);
    }

    @PostMapping("/verify")
    public PaymentResponse verify(@Valid @RequestBody VerifyPaymentRequest request) {
        return paymentService.verify(request);
    }
}
