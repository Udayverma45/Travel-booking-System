package com.example.booking.bus;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripSeatRepository extends JpaRepository<TripSeat, Long> {

    List<TripSeat> findByTripIdOrderById(Long tripId);

    long countByTripIdAndStatus(Long tripId, SeatStatus status);

    List<TripSeat> findByStatusAndLockedUntilBefore(SeatStatus status, LocalDateTime time);

    /** SELECT ... FOR UPDATE: dusra transaction in rows ko tab tak nahi chhoo sakta */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from TripSeat s where s.trip.id = :tripId and s.seatNumber in :seatNumbers")
    List<TripSeat> findSeatsForUpdate(@Param("tripId") Long tripId,
                                      @Param("seatNumbers") List<String> seatNumbers);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from TripSeat s where s.id in :ids")
    List<TripSeat> findAllByIdForUpdate(@Param("ids") List<Long> ids);
}
