package com.example.booking.bus;

import com.example.booking.auth.CurrentUser;
import com.example.booking.common.exception.ConflictException;
import com.example.booking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatLockService {

    public static final int LOCK_MINUTES = 10;

    private final TripSeatRepository tripSeatRepository;
    private final TripRepository tripRepository;
    private final CurrentUser currentUser;

    /**
     * Seats ko 10 min ke liye lock karta hai.
     * SELECT ... FOR UPDATE se race condition (double booking) rukti hai.
     * Ek bhi seat unavailable ho to koi lock nahi hoti (all-or-nothing).
     */
    @Transactional
    public List<SeatResponse> lockSeats(Long tripId, SeatLockRequest request) {
        Long userId = currentUser.get().getId();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + tripId));
        if (trip.getStatus() != TripStatus.SCHEDULED || !trip.getDepartureTime().isAfter(LocalDateTime.now())) {
            throw new ConflictException("This trip is not open for booking");
        }

        List<String> numbers = request.seatNumbers().stream().distinct().toList();
        List<TripSeat> seats = tripSeatRepository.findSeatsForUpdate(tripId, numbers);
        if (seats.size() != numbers.size()) {
            throw new ResourceNotFoundException("One or more seats not found for this trip");
        }

        LocalDateTime now = LocalDateTime.now();
        for (TripSeat seat : seats) {
            boolean booked = seat.getStatus() == SeatStatus.BOOKED;
            boolean lockedByOther = seat.getStatus() == SeatStatus.LOCKED
                    && seat.getLockedUntil() != null
                    && seat.getLockedUntil().isAfter(now)
                    && !userId.equals(seat.getLockedBy());
            if (booked || lockedByOther) {
                throw new ConflictException("Seat " + seat.getSeatNumber() + " is not available");
            }
        }

        for (TripSeat seat : seats) {
            seat.setStatus(SeatStatus.LOCKED);
            seat.setLockedBy(userId);
            seat.setLockedUntil(now.plusMinutes(LOCK_MINUTES));
        }
        return seats.stream()
                .map(s -> new SeatResponse(s.getId(), s.getSeatNumber(), s.getStatus()))
                .toList();
    }
}
