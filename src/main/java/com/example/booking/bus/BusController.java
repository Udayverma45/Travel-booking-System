package com.example.booking.bus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Vendor (bus owner) ke endpoints */
@RestController
@RequestMapping("/api/vendor")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;
    private final TripService tripService;

    @PostMapping("/buses")
    @ResponseStatus(HttpStatus.CREATED)
    public BusResponse addBus(@Valid @RequestBody BusRequest request) {
        return busService.addBus(request);
    }

    @GetMapping("/buses")
    public List<BusResponse> myBuses() {
        return busService.myBuses();
    }

    @PostMapping("/trips")
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse createTrip(@Valid @RequestBody TripRequest request) {
        return tripService.createTrip(request);
    }

    @GetMapping("/trips")
    public List<TripResponse> myTrips() {
        return tripService.myTrips();
    }
}
