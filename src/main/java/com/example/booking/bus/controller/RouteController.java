package com.example.booking.bus.controller;

import com.example.booking.bus.entity.Route;
import com.example.booking.bus.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    public Route addRoute(@RequestBody Route route){
        return routeService.addRoute(route);
    }

    @GetMapping
    public List<Route> getAllRoutes(){
        return routeService.getAllRoute();
    }

    @GetMapping("/{id}")
    public Route getRoute(@PathVariable Long id){
        return routeService.getRouteById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteRoute(@PathVariable Long id){
        routeService.deleteRoute(id);
        return "Route Deleted";
    }
}
