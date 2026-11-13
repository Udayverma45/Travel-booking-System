package com.example.booking.booking;

import com.example.booking.auth.CurrentUser;
import com.example.booking.bus.*;
import com.example.booking.common.exception.BadRequestException;
import com.example.booking.common.exception.ConflictException;
import com.example.booking.common.exception.ForbiddenException;
import com.example.booking.common.exception.ResourceNotFoundException;
import com.example.booking.hotel.Room;
import com.example.booking.hotel.RoomRepository;
import com.example.booking.payment.Payment;
import com.example.booking.payment.PaymentRepository;
import com.example.booking.payment.PaymentStatus;
import com.example.booking.rental.Vehicle;
import com.example.booking.rental.VehicleRepository;
import com.example.booking.rental.VehicleStatus;
import com.example.booking.user.Role;
import com.example.booking.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final TripSeatRepository tripSeatRepository;
    private final RoomRepository roomRepository;
    private final VehicleRepository vehicleRepository;
    private final PaymentRepository paymentRepository;
    private final CurrentUser currentUser;

    // ======================= BUS =======================

    @Transactional
    public BookingResponse createBusBooking(BusBookingRequest req) {
        User user = currentUser.get();
        List<String> numbers = req.seatNumbers().stream().distinct().toList();

        List<TripSeat> seats = tripSeatRepository.findSeatsForUpdate(req.tripId(), numbers);
        if (seats.size() != numbers.size()) {
            throw new ResourceNotFoundException("One or more seats not found");
        }

        LocalDateTime now = LocalDateTime.now();
        for (TripSeat seat : seats) {
            boolean validLock = seat.getStatus() == SeatStatus.LOCKED
                    && user.getId().equals(seat.getLockedBy())
                    && seat.getLockedUntil() != null
                    && seat.getLockedUntil().isAfter(now);
            if (!validLock) {
                throw new ConflictException(
                        "Seat " + seat.getSeatNumber() + " is not locked by you or lock expired");
            }
        }

        Trip trip = seats.get(0).getTrip();
        if (!trip.getDepartureTime().isAfter(now)) {
            throw new ConflictException("Trip already departed");
        }
        BigDecimal fare = trip.getBaseFare();
        Long vendorId = trip.getBus().getVendor() == null ? null : trip.getBus().getVendor().getId();

        Booking booking = newBooking(user.getId(), vendorId, BookingType.BUS,
                fare.multiply(BigDecimal.valueOf(seats.size())));
        for (TripSeat seat : seats) {
            addItem(booking, seat.getId(), "Seat " + seat.getSeatNumber(), 1, fare, null, null);
        }
        return toResponse(bookingRepository.save(booking));
    }

    // ======================= HOTEL =======================

    @Transactional
    public BookingResponse createHotelBooking(HotelBookingRequest req) {
        User user = currentUser.get();
        validateDates(req.checkIn(), req.checkOut());

        // room row lock: do log ek saath last room na le jayein
        Room room = roomRepository.findByIdForUpdate(req.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + req.roomId()));

        long booked = bookingItemRepository.sumOverlap(BookingType.HOTEL, room.getId(),
                req.checkIn(), req.checkOut(), BookingStatus.ACTIVE);
        long available = room.getTotalRooms() - booked;
        if (req.rooms() > available) {
            throw new ConflictException("Only " + Math.max(available, 0) + " room(s) available for these dates");
        }

        long nights = ChronoUnit.DAYS.between(req.checkIn(), req.checkOut());
        BigDecimal total = room.getPricePerNight()
                .multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(req.rooms()));

        Booking booking = newBooking(user.getId(), room.getHotel().getVendor().getId(), BookingType.HOTEL, total);
        addItem(booking, room.getId(),
                room.getHotel().getName() + " - " + room.getRoomType() + " x" + req.rooms() + " (" + nights + " nights)",
                req.rooms(), total, req.checkIn(), req.checkOut());
        return toResponse(bookingRepository.save(booking));
    }

    // ======================= RENTAL =======================

    @Transactional
    public BookingResponse createRentalBooking(RentalBookingRequest req) {
        User user = currentUser.get();
        validateDates(req.startDate(), req.endDate());

        Vehicle vehicle = vehicleRepository.findByIdForUpdate(req.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + req.vehicleId()));
        if (vehicle.getStatus() != VehicleStatus.ACTIVE) {
            throw new ConflictException("Vehicle is not available for rent");
        }

        long clash = bookingItemRepository.sumOverlap(BookingType.RENTAL, vehicle.getId(),
                req.startDate(), req.endDate(), BookingStatus.ACTIVE);
        if (clash > 0) {
            throw new ConflictException("Vehicle is already booked for these dates");
        }

        long days = ChronoUnit.DAYS.between(req.startDate(), req.endDate());
        BigDecimal total = vehicle.getPricePerDay().multiply(BigDecimal.valueOf(days));

        Booking booking = newBooking(user.getId(), vehicle.getVendor().getId(), BookingType.RENTAL, total);
        addItem(booking, vehicle.getId(),
                vehicle.getModel() + " (" + vehicle.getType() + ") for " + days + " day(s)",
                1, total, req.startDate(), req.endDate());
        return toResponse(bookingRepository.save(booking));
    }

    // ======================= CONFIRM / CANCEL =======================

    /** Payment success ke baad PaymentService isse call karta hai */
    @Transactional
    public Booking confirm(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Booking is already " + booking.getStatus());
        }

        if (booking.getType() == BookingType.BUS) {
            List<TripSeat> seats = tripSeatRepository.findAllByIdForUpdate(itemRefIds(booking));
            LocalDateTime now = LocalDateTime.now();
            for (TripSeat seat : seats) {
                boolean ok = seat.getStatus() == SeatStatus.LOCKED
                        && booking.getUserId().equals(seat.getLockedBy())
                        && seat.getLockedUntil() != null
                        && seat.getLockedUntil().isAfter(now);
                if (!ok) {
                    throw new ConflictException("Seat lock expired, please select seats again");
                }
            }
            for (TripSeat seat : seats) {
                seat.setStatus(SeatStatus.BOOKED);
                seat.setLockedUntil(null);
            }
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        return booking;
    }

    @Transactional
    public BookingResponse cancel(Long bookingId) {
        User user = currentUser.get();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        if (!booking.getUserId().equals(user.getId())) {
            throw new ForbiddenException("Not your booking");
        }
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ConflictException("Booking is already " + booking.getStatus());
        }

        boolean confirmed = booking.getStatus() == BookingStatus.CONFIRMED;
        BigDecimal refund = BigDecimal.ZERO;

        if (booking.getType() == BookingType.BUS) {
            List<TripSeat> seats = tripSeatRepository.findAllByIdForUpdate(itemRefIds(booking));
            if (confirmed) {
                LocalDateTime departure = seats.get(0).getTrip().getDepartureTime();
                long hours = Duration.between(LocalDateTime.now(), departure).toHours();
                if (hours < 0) {
                    throw new ConflictException("Trip already departed");
                }
                int percent = hours >= 24 ? 100 : hours >= 6 ? 50 : 0;
                refund = percentOf(booking.getTotalAmount(), percent);
            }
            for (TripSeat seat : seats) {
                // sirf apni booked/locked seats chhodo (expired lock kisi aur ne le li ho sakti hai)
                if (seat.getStatus() == SeatStatus.BOOKED || user.getId().equals(seat.getLockedBy())) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seat.setLockedBy(null);
                    seat.setLockedUntil(null);
                }
            }
        } else if (confirmed) {
            LocalDate start = booking.getItems().get(0).getStartDate();
            long days = ChronoUnit.DAYS.between(LocalDate.now(), start);
            int percent = days >= 2 ? 100 : days >= 1 ? 50 : 0;
            refund = percentOf(booking.getTotalAmount(), percent);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setRefundAmount(refund);
        if (confirmed) {
            paymentRepository.findFirstByBookingIdAndStatus(bookingId, PaymentStatus.SUCCESS)
                    .ifPresent((Payment p) -> p.setStatus(PaymentStatus.REFUNDED));
        }
        return toResponse(booking);
    }

    // ======================= READ =======================

    @Transactional(readOnly = true)
    public List<BookingResponse> myBookings() {
        Long userId = currentUser.get().getId();
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long id) {
        User user = currentUser.get();
        Booking b = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        if (!b.getUserId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Not your booking");
        }
        return toResponse(b);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> vendorBookings(Long vendorId) {
        return bookingRepository.findByVendorIdOrderByCreatedAtDesc(vendorId).stream().map(this::toResponse).toList();
    }

    // ======================= helpers =======================

    private Booking newBooking(Long userId, Long vendorId, BookingType type, BigDecimal total) {
        Booking b = new Booking();
        b.setUserId(userId);
        b.setVendorId(vendorId);
        b.setType(type);
        b.setStatus(BookingStatus.PENDING);
        b.setTotalAmount(total);
        b.setCreatedAt(LocalDateTime.now());
        return b;
    }

    private void addItem(Booking booking, Long refId, String description, int qty, BigDecimal price,
                         LocalDate start, LocalDate end) {
        BookingItem item = new BookingItem();
        item.setBooking(booking);
        item.setItemRefId(refId);
        item.setDescription(description);
        item.setQuantity(qty);
        item.setPrice(price);
        item.setStartDate(start);
        item.setEndDate(end);
        booking.getItems().add(item);
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start.isBefore(LocalDate.now())) {
            throw new BadRequestException("Start date cannot be in the past");
        }
        if (!end.isAfter(start)) {
            throw new BadRequestException("End date must be after start date");
        }
    }

    private List<Long> itemRefIds(Booking b) {
        return b.getItems().stream().map(BookingItem::getItemRefId).toList();
    }

    private BigDecimal percentOf(BigDecimal amount, int percent) {
        return amount.multiply(BigDecimal.valueOf(percent)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BookingResponse toResponse(Booking b) {
        List<BookingItemResponse> items = b.getItems().stream()
                .map(i -> new BookingItemResponse(i.getDescription(), i.getQuantity(), i.getPrice(),
                        i.getStartDate(), i.getEndDate()))
                .toList();
        return new BookingResponse(b.getId(), b.getType(), b.getStatus(), b.getTotalAmount(),
                b.getRefundAmount(), b.getCreatedAt(), items);
    }
}
