package com.example.booking.bus.repository;

import com.example.booking.bus.entity.Route;
import com.example.booking.bus.entity.Trip;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip , Long> {

    List<Trip> findByRouteAndDepartureTimeBetween(
            Route route, LocalDateTime start, LocalDateTime end );
}
