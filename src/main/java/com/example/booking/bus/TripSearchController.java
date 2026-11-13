package com.example.booking.bus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Customer ke endpoints: search, seat map, seat lock */
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripSearchController {

    private final TripService tripService;
    private final SeatLockService seatLockService;

    @GetMapping("/search")
    public List<TripResponse> search(@RequestParam String source,
                                     @RequestParam String destination,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return tripService.search(source, destination, date);
    }

    @GetMapping("/{id}/seats")
    public List<SeatResponse> seats(@PathVariable Long id) {
        return tripService.getSeats(id);
    }

    @PostMapping("/{id}/seats/lock")
    public List<SeatResponse> lock(@PathVariable Long id, @Valid @RequestBody SeatLockRequest request) {
        return seatLockService.lockSeats(id, request);
    }
}
