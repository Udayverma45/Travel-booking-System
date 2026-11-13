package com.example.booking.rental;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // ---- vendor ----
    @PostMapping("/api/vendor/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse add(@Valid @RequestBody VehicleRequest request) {
        return vehicleService.add(request);
    }

    @GetMapping("/api/vendor/vehicles")
    public List<VehicleResponse> mine() {
        return vehicleService.mine();
    }

    @PatchMapping("/api/vendor/vehicles/{id}/status")
    public VehicleResponse status(@PathVariable Long id, @RequestParam VehicleStatus status) {
        return vehicleService.setStatus(id, status);
    }

    // ---- customer (public) ----
    @GetMapping("/api/rentals/search")
    public List<VehicleResponse> search(
            @RequestParam String city,
            @RequestParam(required = false) VehicleType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return vehicleService.search(city, type, startDate, endDate);
    }
}
