package com.example.booking.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/bus")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse bus(@Valid @RequestBody BusBookingRequest request) {
        return bookingService.createBusBooking(request);
    }

    @PostMapping("/hotel")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse hotel(@Valid @RequestBody HotelBookingRequest request) {
        return bookingService.createHotelBooking(request);
    }

    @PostMapping("/rental")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse rental(@Valid @RequestBody RentalBookingRequest request) {
        return bookingService.createRentalBooking(request);
    }

    @GetMapping("/my")
    public List<BookingResponse> my() {
        return bookingService.myBookings();
    }

    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @PostMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id) {
        return bookingService.cancel(id);
    }
}
