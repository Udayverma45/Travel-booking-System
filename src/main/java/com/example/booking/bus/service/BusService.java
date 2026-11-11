package com.example.booking.bus.service;

import com.example.booking.bus.dto.BusRequest;
import com.example.booking.bus.entity.Bus;
import com.example.booking.bus.repository.BusRepository;
import com.example.booking.common.exception.ConflictException;
import com.example.booking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;

    public Bus addBus(BusRequest request){
        if (busRepository.existsByBusNumber(request.busNumber())){
            throw new ConflictException("Bus number alrady exists: " + request.busNumber());
        }
        Bus bus = new Bus();
        bus.setBusNumber(request.busNumber());
        bus.setBusType(request.busType());
        bus.setTotalSeats(request.totalSeats());
        return busRepository.save(bus);
    }

    public List<Bus> getAllBuses(){
        return busRepository.findAll();
    }

    public Bus getBusById(Long id){
        return busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not Fount with id : " + id));
    }

    public void deletBus(Long id){
        busRepository.deleteById(id);
    }
}
