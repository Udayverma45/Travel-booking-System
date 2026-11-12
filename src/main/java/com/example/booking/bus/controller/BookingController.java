package com.example.booking.bus.controller;

import com.example.booking.bus.dto.BookingRequest;
import com.example.booking.bus.dto.BookingResponse;
import com.example.booking.bus.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse create(@Valid @RequestBody BookingRequest request){
        return bookingService.createBooking(request);
    }
    @PostMapping("/{id}/confirm")
    public BookingResponse confirm(@PathVariable Long id){
        return bookingService.confirmBooking(id);
    }
}
