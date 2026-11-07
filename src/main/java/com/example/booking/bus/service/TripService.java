package com.example.booking.bus.service;

import com.example.booking.bus.dto.TripRequest;
import com.example.booking.bus.entity.*;
import com.example.booking.bus.repository.TripRepository;
import com.example.booking.bus.repository.TripSeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public Trip createTrip(TripRequest request){
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
        return savedTrip;
    }

    public List<Trip> getAllTrips(){
        return tripRepository.findAll();
    }

    public List<TripSeat> getSeatsOfTrip(Long tripId){
        return tripSeatRepository.findByTripId(tripId);
    }
}
