package com.example.booking.bus.service;

import com.example.booking.booking.entity.Booking;
import com.example.booking.booking.entity.BookingStatus;
import com.example.booking.bus.entity.SeatStatus;
import com.example.booking.bus.entity.TripSeat;
import com.example.booking.bus.repository.BookingRepository;
import com.example.booking.bus.repository.TripSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatLockCleanupJob {

    private final TripSeatRepository tripSeatRepository;
    private final BookingRepository bookingRepository;

    @Scheduled(fixedRate = 60000)   // har 1 minute
    @Transactional
    public void releaseExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();

        List<TripSeat> expiredSeats =
                tripSeatRepository.findByStatusAndLockedUntilBefore(SeatStatus.LOCKED, now);
        for (TripSeat seat : expiredSeats) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockedBy(null);
            seat.setLockedUntil(null);
        }

        List<Booking> staleBookings =
                bookingRepository.findByStatusAndCreatedAtBefore(
                        BookingStatus.PENDING, now.minusMinutes(10));
        for (Booking b : staleBookings) {
            b.setStatus(BookingStatus.EXPIRED);
        }

        log.info("Cleanup: {} seats released, {} bookings expired",
                expiredSeats.size(), staleBookings.size());
    }
}
