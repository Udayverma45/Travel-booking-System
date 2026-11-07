package com.example.booking.bus.controller;

import com.example.booking.bus.dto.TripRequest;
import com.example.booking.bus.entity.Trip;
import com.example.booking.bus.entity.TripSeat;
import com.example.booking.bus.service.TripService;
import lombok.Lombok;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public Trip createTrip(@RequestBody TripRequest request){
        return tripService.createTrip(request);
    }

    @GetMapping
    public List<Trip> getAllTrips(){
        return tripService.getAllTrips();
    }

    @GetMapping("/{id}/seats")
    public List<TripSeat> getSeats(@PathVariable Long id){
        return tripService.getSeatsOfTrip(id);
    }
}
