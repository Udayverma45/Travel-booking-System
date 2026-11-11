package com.example.booking.bus.controller;

import com.example.booking.bus.dto.SeatLockRequest;
import com.example.booking.bus.dto.SeatResponse;
import com.example.booking.bus.dto.TripRequest;
import com.example.booking.bus.dto.TripResponse;
import com.example.booking.bus.entity.Trip;
import com.example.booking.bus.entity.TripSeat;
import com.example.booking.bus.service.SeatLockService;
import com.example.booking.bus.service.TripService;
import jakarta.validation.Valid;
import lombok.Lombok;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final SeatLockService seatLockService;

    @PostMapping
    public TripResponse createTrip(@RequestBody TripRequest request){
        return tripService.createTrip(request);
    }

    @GetMapping
    public List<TripResponse> getAllTrips(){
        return tripService.getAllTrips();
    }

    @GetMapping("/{id}/seats")
    public List<SeatResponse> getSeats(@PathVariable Long id){
        return tripService.getSeatsOfTrip(id);
    }
    @PostMapping("/{id}/seats/lock")
    public List<SeatResponse> lockSeats(@PathVariable Long id,
                                        @Valid @RequestBody SeatLockRequest request) {
        return seatLockService.lockSeats(id, request);
    }
}
