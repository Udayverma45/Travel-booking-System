package com.example.booking.booking;

import com.example.booking.bus.SeatLockService;
import com.example.booking.bus.SeatStatus;
import com.example.booking.bus.TripSeat;
import com.example.booking.bus.TripSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Har minute: expired seat locks chhodta hai aur purani PENDING bookings ko EXPIRED karta hai */
@Component
@RequiredArgsConstructor
@Slf4j
public class CleanupJob {

    private final TripSeatRepository tripSeatRepository;
    private final BookingRepository bookingRepository;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void cleanup() {
        LocalDateTime now = LocalDateTime.now();

        List<TripSeat> expiredSeats = tripSeatRepository.findByStatusAndLockedUntilBefore(SeatStatus.LOCKED, now);
        for (TripSeat seat : expiredSeats) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockedBy(null);
            seat.setLockedUntil(null);
        }

        List<Booking> stale = bookingRepository.findByStatusAndCreatedAtBefore(
                BookingStatus.PENDING, now.minusMinutes(SeatLockService.LOCK_MINUTES));
        for (Booking b : stale) {
            b.setStatus(BookingStatus.EXPIRED);
        }

        if (!expiredSeats.isEmpty() || !stale.isEmpty()) {
            log.info("Cleanup: {} seats released, {} bookings expired", expiredSeats.size(), stale.size());
        }
    }
}
