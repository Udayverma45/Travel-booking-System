package com.example.booking.bus.repository;

import com.example.booking.bus.entity.SeatStatus;
import com.example.booking.bus.entity.TripSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripSeatRepository extends JpaRepository<TripSeat, Long> {

    List<TripSeat> findByTripId(Long tripId);

    List<TripSeat> findByTripIdAndStatus(Long tripId, SeatStatus status);
}

