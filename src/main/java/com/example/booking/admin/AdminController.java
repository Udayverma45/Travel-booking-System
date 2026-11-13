package com.example.booking.admin;

import com.example.booking.booking.BookingRepository;
import com.example.booking.booking.BookingStatus;
import com.example.booking.user.UserRepository;
import com.example.booking.vendor.VendorRepository;
import com.example.booking.vendor.VendorResponse;
import com.example.booking.vendor.VendorService;
import com.example.booking.vendor.VendorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final VendorService vendorService;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final BookingRepository bookingRepository;

    @GetMapping("/vendors/pending")
    public List<VendorResponse> pending() {
        return vendorService.getByStatus(VendorStatus.PENDING);
    }

    @PatchMapping("/vendors/{id}/approve")
    public VendorResponse approve(@PathVariable Long id) {
        return vendorService.updateStatus(id, VendorStatus.APPROVED);
    }

    @PatchMapping("/vendors/{id}/reject")
    public VendorResponse reject(@PathVariable Long id) {
        return vendorService.updateStatus(id, VendorStatus.REJECTED);
    }

    @GetMapping("/stats")
    public PlatformStatsResponse stats() {
        return new PlatformStatsResponse(
                userRepository.count(),
                vendorRepository.count(),
                vendorRepository.countByStatus(VendorStatus.PENDING),
                bookingRepository.count(),
                bookingRepository.countByStatus(BookingStatus.CONFIRMED),
                bookingRepository.totalRevenue(BookingStatus.CONFIRMED));
    }
}
