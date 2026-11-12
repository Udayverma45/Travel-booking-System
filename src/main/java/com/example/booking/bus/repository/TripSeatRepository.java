package com.example.booking.bus.repository;

import com.example.booking.bus.entity.SeatStatus;
import com.example.booking.bus.entity.TripSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripSeatRepository extends JpaRepository<TripSeat, Long> {

    List<TripSeat> findByTripId(Long tripId);

    List<TripSeat> findByTripIdAndStatus(Long tripId, SeatStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM TripSeat s WHERE s.trip.id = :tripId AND s.seatNumber IN :seatNumbers")
    List<TripSeat> findSeatsForUpdate(@Param("tripId") Long tripId,
                                      @Param("seatNumbers") List<String> seatNumbers);

    List<TripSeat> findByStatusAndLockedUntilBefore(SeatStatus status, LocalDateTime time);
}

