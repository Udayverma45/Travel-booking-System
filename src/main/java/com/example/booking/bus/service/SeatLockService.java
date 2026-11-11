package com.example.booking.bus.service;

import com.example.booking.bus.dto.SeatLockRequest;
import com.example.booking.bus.dto.SeatResponse;
import com.example.booking.bus.entity.SeatStatus;
import com.example.booking.bus.entity.TripSeat;
import com.example.booking.bus.repository.TripSeatRepository;
import com.example.booking.common.exception.ConflictException;
import com.example.booking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class  SeatLockService {

    private static final int LOCK_MINUTES = 10;

    private final TripSeatRepository tripSeatRepository;

    @Transactional
    public List<SeatResponse> lockSeats(Long tripId, SeatLockRequest request) {

        List<TripSeat> seats = tripSeatRepository
                .findSeatsForUpdate(tripId, request.seatNumbers());

        if (seats.size() != request.seatNumbers().size()) {
            throw new ResourceNotFoundException("One or more seats not found for this trip");
        }

        LocalDateTime now = LocalDateTime.now();

        for (TripSeat seat : seats) {
            boolean isBooked = seat.getStatus() == SeatStatus.BOOKED;
            boolean isActivelyLocked = seat.getStatus() == SeatStatus.LOCKED
                    && seat.getLockedUntil() != null
                    && seat.getLockedUntil().isAfter(now);

            if (isBooked || isActivelyLocked) {
                throw new ConflictException("Seat " + seat.getSeatNumber() + " is not available");
            }
        }

        for (TripSeat seat : seats) {
            seat.setStatus(SeatStatus.LOCKED);
            seat.setLockedBy(request.userId());
            seat.setLockedUntil(now.plusMinutes(LOCK_MINUTES));
        }

        return seats.stream()
                .map(s -> new SeatResponse(s.getId(), s.getSeatNumber(), s.getStatus()))
                .toList();
    }
}
