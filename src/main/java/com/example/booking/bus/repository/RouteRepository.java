package com.example.booking.bus.repository;

import com.example.booking.bus.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findBySourceAndDestination(String source, String destination);
}
