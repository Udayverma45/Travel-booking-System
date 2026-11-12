package com.example.booking.bus.repository;

import com.example.booking.booking.entity.Booking;
import com.example.booking.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking , Long> {
    List<Booking> findByUserId(Long userId);

    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime time);
}
