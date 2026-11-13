package com.example.booking.bus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Route add(@Valid @RequestBody RouteRequest request) {
        return routeService.addRoute(request);
    }

    @GetMapping
    public List<Route> all() {
        return routeService.getAll();
    }
}
