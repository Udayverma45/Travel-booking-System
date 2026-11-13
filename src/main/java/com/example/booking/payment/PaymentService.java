package com.example.booking.payment;

import com.example.booking.auth.CurrentUser;
import com.example.booking.booking.Booking;
import com.example.booking.booking.BookingRepository;
import com.example.booking.booking.BookingService;
import com.example.booking.booking.BookingStatus;
import com.example.booking.common.exception.ConflictException;
import com.example.booking.common.exception.ForbiddenException;
import com.example.booking.common.exception.ResourceNotFoundException;
import com.example.booking.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abhi MOCK payment gateway hai (test ke liye).
 * Real Razorpay ke liye: createOrder me Razorpay Orders API call karo aur verify me
 * HMAC signature check karo; baaki flow (booking confirm) same rahega.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final CurrentUser currentUser;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest req) {
        User user = currentUser.get();
        Booking booking = bookingRepository.findById(req.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + req.bookingId()));
        if (!booking.getUserId().equals(user.getId())) {
            throw new ForbiddenException("Not your booking");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Booking is " + booking.getStatus() + ", payment not possible");
        }

        // pehle se CREATED order ho to wahi wapas do
        Payment payment = paymentRepository
                .findFirstByBookingIdAndStatus(booking.getId(), PaymentStatus.CREATED)
                .orElseGet(() -> {
                    Payment p = new Payment();
                    p.setBooking(booking);
                    p.setProviderOrderId("order_" + UUID.randomUUID().toString().replace("-", ""));
                    p.setAmount(booking.getTotalAmount());
                    p.setStatus(PaymentStatus.CREATED);
                    p.setCreatedAt(LocalDateTime.now());
                    return paymentRepository.save(p);
                });
        return new CreateOrderResponse(booking.getId(), payment.getProviderOrderId(), payment.getAmount(), "INR");
    }

    @Transactional
    public PaymentResponse verify(VerifyPaymentRequest req) {
        User user = currentUser.get();
        Payment payment = paymentRepository.findByProviderOrderId(req.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + req.orderId()));
        Booking booking = payment.getBooking();
        if (!booking.getUserId().equals(user.getId())) {
            throw new ForbiddenException("Not your booking");
        }
        if (payment.getStatus() != PaymentStatus.CREATED) {
            throw new ConflictException("Payment already " + payment.getStatus());
        }

        if (!req.success()) {
            payment.setStatus(PaymentStatus.FAILED);
            return new PaymentResponse(booking.getId(), payment.getProviderOrderId(),
                    payment.getStatus(), booking.getStatus(), payment.getAmount());
        }

        // confirm fail ho (lock expire) to poora transaction rollback hota hai
        Booking confirmed = bookingService.confirm(booking.getId());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProviderPaymentId(req.paymentId() != null ? req.paymentId()
                : "pay_" + UUID.randomUUID().toString().replace("-", ""));
        payment.setPaidAt(LocalDateTime.now());
        return new PaymentResponse(confirmed.getId(), payment.getProviderOrderId(),
                payment.getStatus(), confirmed.getStatus(), payment.getAmount());
    }
}
