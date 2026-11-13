package com.example.booking.vendor;

import com.example.booking.booking.BookingRepository;
import com.example.booking.booking.BookingResponse;
import com.example.booking.booking.BookingService;
import com.example.booking.booking.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vendor")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    @GetMapping("/me")
    public VendorResponse me() {
        return vendorService.me();
    }

    @GetMapping("/bookings")
    public List<BookingResponse> bookings() {
        return bookingService.vendorBookings(vendorService.currentVendor().getId());
    }

    @GetMapping("/dashboard")
    public VendorDashboardResponse dashboard() {
        Long vid = vendorService.currentVendor().getId();
        return new VendorDashboardResponse(
                bookingRepository.countByVendorId(vid),
                bookingRepository.countByVendorIdAndStatus(vid, BookingStatus.CONFIRMED),
                bookingRepository.countByVendorIdAndStatus(vid, BookingStatus.CANCELLED),
                bookingRepository.revenueOfVendor(vid, BookingStatus.CONFIRMED));
    }
}
