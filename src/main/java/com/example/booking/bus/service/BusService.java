package com.example.booking.bus.service;

import com.example.booking.bus.entity.Bus;
import com.example.booking.bus.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;

    public Bus addBus(Bus bus){
        return busRepository.save(bus);
    }

    public List<Bus> getAllBuses(){
        return busRepository.findAll();
    }

    public Bus getBusById(Long id){
        return busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not Fount with id : " + id));
    }

    public void deletBus(Long id){
        busRepository.deleteById(id);
    }
}
