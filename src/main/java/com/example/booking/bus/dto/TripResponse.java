package com.example.booking.bus.dto;

import com.example.booking.bus.entity.BusType;
import com.example.booking.bus.entity.TripStatus;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripResponse(
        Long id,
        String busNumber,
        BusType busType,
        String source,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        BigDecimal baseFare,
        TripStatus status
   )
{

}
