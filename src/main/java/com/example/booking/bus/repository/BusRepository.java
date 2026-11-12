package com.example.booking.bus.repository;

import com.example.booking.booking.entity.BookingStatus;
import com.example.booking.bus.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BusRepository extends JpaRepository<Bus, Long> {
    boolean existsByBusNumber(String busNumber);


}
