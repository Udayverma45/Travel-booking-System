package com.example.booking.bus.service;

import com.example.booking.booking.entity.Booking;
import com.example.booking.booking.entity.BookingItem;
import com.example.booking.booking.entity.BookingStatus;
import com.example.booking.booking.entity.BookingType;
import com.example.booking.bus.dto.BookingRequest;
import com.example.booking.bus.dto.BookingResponse;
import com.example.booking.bus.entity.SeatStatus;
import com.example.booking.bus.entity.TripSeat;
import com.example.booking.bus.repository.BookingRepository;
import com.example.booking.bus.repository.TripSeatRepository;
import com.example.booking.common.exception.ConflictException;
import com.example.booking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripSeatRepository tripSeatRepository;

    @Transactional
    public BookingResponse createBooking(BookingRequest request){
        List<TripSeat> seats = tripSeatRepository.findSeatsForUpdate(request.tripId(), request.seatNumbers());

        if(seats.size() != request.seatNumbers().size()){
            throw new ResourceNotFoundException("One or more seats not found");
        }
        LocalDateTime now = LocalDateTime.now();
        for(TripSeat seat : seats){
            boolean validLock = seat.getStatus() == SeatStatus.LOCKED
               && request.userId().equals(seat.getLockedBy())
               && seat.getLockedUntil() !=null
               && seat.getLockedUntil().isAfter(now);
        if(!validLock){
            throw new ConflictException(
                    "Seat " + seat.getSeatNumber() + " is not locked by you or locked expired" );
            }
        }
        BigDecimal fare = seats.get(0).getTrip().getBaseFare();

        Booking booking = new Booking();
        booking.setUserId(request.userId());
        booking.setType(BookingType.BUS);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(now);
        booking.setTotalAmount(fare.multiply(BigDecimal.valueOf(seats.size())));

        for (TripSeat seat : seats) {
            BookingItem item = new BookingItem();
            item.setBooking(booking);
            item.setItemRefId(seat.getId());
            item.setPrice(fare);
            booking.getItems().add(item);
        }

        Booking saved = bookingRepository.save(booking);
        return toResponse(saved, request.seatNumbers());
    }

    @Transactional
    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Booking is already " + booking.getStatus());
        }

        List<Long> seatIds = booking.getItems().stream()
                .map(BookingItem::getItemRefId).toList();
        List<TripSeat> seats = tripSeatRepository.findAllById(seatIds);

        LocalDateTime now = LocalDateTime.now();
        for (TripSeat seat : seats) {
            if (seat.getLockedUntil() == null || seat.getLockedUntil().isBefore(now)) {
                booking.setStatus(BookingStatus.EXPIRED);
                throw new ConflictException("Seat lock expired, please select seats again");
            }
        }

        // fake payment success
        for (TripSeat seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
            seat.setLockedUntil(null);
        }
        booking.setStatus(BookingStatus.CONFIRMED);

        List<String> seatNumbers = seats.stream().map(TripSeat::getSeatNumber).toList();
        return toResponse(booking, seatNumbers);
    }

    private BookingResponse toResponse(Booking b, List<String> seatNumbers) {
        return new BookingResponse(b.getId(), b.getType(), b.getStatus(),
                b.getTotalAmount(), seatNumbers, b.getCreatedAt());
    }
}