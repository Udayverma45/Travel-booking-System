package com.example.booking.bus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    public Route addRoute(RouteRequest request) {
        Route route = new Route();
        route.setSource(request.source().trim());
        route.setDestination(request.destination().trim());
        route.setDistanceKm(request.distanceKm());
        return routeRepository.save(route);
    }

    public List<Route> getAll() {
        return routeRepository.findAll();
    }
}
