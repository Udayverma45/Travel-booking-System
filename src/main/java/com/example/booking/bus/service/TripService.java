package com.example.booking.bus.service;

import com.example.booking.bus.dto.SeatResponse;
import com.example.booking.bus.dto.TripRequest;
import com.example.booking.bus.dto.TripResponse;
import com.example.booking.bus.entity.*;
import com.example.booking.bus.repository.TripRepository;
import com.example.booking.bus.repository.TripSeatRepository;
//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripSeatRepository tripSeatRepository;
    private final BusService busService;
    private final RouteService routeService;

    @Transactional
    public TripResponse createTrip(TripRequest request){
        Bus bus = busService.getBusById(request.busId());
        Route route = routeService.getRouteById(request.routeId());

        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setRoute(route);
        trip.setArrivalTime(request.arrivalTime());
        trip.setDepartureTime(request.departureTime());
        trip.setBaseFare(request.baseFare());
        trip.setStatus(TripStatus.SCHEDULE);

        Trip savedTrip = tripRepository.save(trip);

        List<TripSeat> seats = new ArrayList<>();
        for(int i=1; i<=bus.getTotalSeats(); i++){
            TripSeat seat = new TripSeat();
            seat.setTrip(savedTrip);
            seat.setSeatNumber(String.valueOf(i));
            seat.setStatus(SeatStatus.AVAILABLE);
            seats.add(seat);
        }
        tripSeatRepository.saveAll(seats);
        return toResponse(savedTrip);
    }
    @Transactional(readOnly = true)
    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatsOfTrip(Long tripId) {
        return tripSeatRepository.findByTripId(tripId).stream()
                .map(this::toSeatResponse)
                .toList();
    }


    private TripResponse toResponse(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getBus().getBusNumber(),
                trip.getBus().getBusType(),
                trip.getRoute().getSource(),
                trip.getRoute().getDestination(),
                trip.getDepartureTime(),
                trip.getArrivalTime(),
                trip.getBaseFare(),
                trip.getStatus()
        );
    }

    private SeatResponse toSeatResponse(TripSeat seat) {
        return new SeatResponse(seat.getId(), seat.getSeatNumber(), seat.getStatus());
    }
}
