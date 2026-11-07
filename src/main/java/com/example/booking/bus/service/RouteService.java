package com.example.booking.bus.service;

import com.example.booking.bus.entity.Route;
import com.example.booking.bus.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    public Route addRoute(Route route){
        return routeRepository.save(route);
    }

    public List<Route> getAllRoute(){
        return routeRepository.findAll();
    }

    public Route getRouteById(Long id){
        return routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id : " + id));
    }

    public void deleteRoute(Long id){
        routeRepository.deleteById(id);
    }
}
