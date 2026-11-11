package com.example.booking.bus.controller;

import com.example.booking.bus.dto.BusRequest;
import com.example.booking.bus.entity.Bus;
import com.example.booking.bus.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;

    @PostMapping
    public Bus addBus(@Valid @RequestBody BusRequest request){
        return busService.addBus(request);
    }

    @GetMapping
    public List<Bus> getAllBuses(){
        return busService.getAllBuses();
    }

    @GetMapping("/{id}")
    public Bus getBus(@PathVariable Long id){
        return busService.getBusById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteBus(@PathVariable Long id){
        busService.deletBus(id);
        return "Bus Deleted";
    }

}
