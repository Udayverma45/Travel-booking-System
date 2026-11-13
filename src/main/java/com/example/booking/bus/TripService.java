package com.example.booking.bus;

import com.example.booking.common.exception.BadRequestException;
import com.example.booking.common.exception.ForbiddenException;
import com.example.booking.common.exception.ResourceNotFoundException;
import com.example.booking.vendor.Vendor;
import com.example.booking.vendor.VendorService;
import com.example.booking.vendor.VendorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripSeatRepository tripSeatRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final VendorService vendorService;

    @Transactional
    public TripResponse createTrip(TripRequest request) {
        Vendor vendor = vendorService.approvedVendor(VendorType.BUS);

        Bus bus = busRepository.findById(request.busId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found: " + request.busId()));
        if (bus.getVendor() == null || !bus.getVendor().getId().equals(vendor.getId())) {
            throw new ForbiddenException("This bus does not belong to you");
        }
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + request.routeId()));

        if (!request.departureTime().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Departure time must be in the future");
        }
        if (!request.arrivalTime().isAfter(request.departureTime())) {
            throw new BadRequestException("Arrival time must be after departure time");
        }

        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setRoute(route);
        trip.setDepartureTime(request.departureTime());
        trip.setArrivalTime(request.arrivalTime());
        trip.setBaseFare(request.baseFare());
        trip.setStatus(TripStatus.SCHEDULED);
        Trip saved = tripRepository.save(trip);

        // bus ki totalSeats ke hisaab se har seat ki row
        List<TripSeat> seats = new ArrayList<>();
        for (int i = 1; i <= bus.getTotalSeats(); i++) {
            TripSeat seat = new TripSeat();
            seat.setTrip(saved);
            seat.setSeatNumber(String.valueOf(i));
            seat.setStatus(SeatStatus.AVAILABLE);
            seats.add(seat);
        }
        tripSeatRepository.saveAll(seats);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TripResponse> myTrips() {
        Long vendorId = vendorService.currentVendor().getId();
        return tripRepository.findByBusVendorId(vendorId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TripResponse> search(String source, String destination, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return tripRepository.search(source.trim(), destination.trim(), start, start.plusDays(1),
                        TripStatus.SCHEDULED).stream()
                .filter(t -> t.getDepartureTime().isAfter(now))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeats(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new ResourceNotFoundException("Trip not found: " + tripId);
        }
        return tripSeatRepository.findByTripIdOrderById(tripId).stream()
                .map(s -> new SeatResponse(s.getId(), s.getSeatNumber(), s.getStatus()))
                .toList();
    }

    private TripResponse toResponse(Trip t) {
        return new TripResponse(t.getId(), t.getBus().getBusNumber(), t.getBus().getBusType(),
                t.getRoute().getSource(), t.getRoute().getDestination(),
                t.getDepartureTime(), t.getArrivalTime(), t.getBaseFare(), t.getStatus(),
                tripSeatRepository.countByTripIdAndStatus(t.getId(), SeatStatus.AVAILABLE));
    }
}
