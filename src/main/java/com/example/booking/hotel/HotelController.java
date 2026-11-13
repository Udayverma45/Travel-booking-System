package com.example.booking.hotel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    // ---- vendor ----
    @PostMapping("/api/vendor/hotels")
    @ResponseStatus(HttpStatus.CREATED)
    public HotelResponse add(@Valid @RequestBody HotelRequest request) {
        return hotelService.addHotel(request);
    }

    @GetMapping("/api/vendor/hotels")
    public List<HotelResponse> mine() {
        return hotelService.myHotels();
    }

    @PostMapping("/api/vendor/hotels/{hotelId}/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse addRoom(@PathVariable Long hotelId, @Valid @RequestBody RoomRequest request) {
        return hotelService.addRoom(hotelId, request);
    }

    @GetMapping("/api/vendor/hotels/{hotelId}/rooms")
    public List<RoomResponse> rooms(@PathVariable Long hotelId) {
        return hotelService.roomsOfHotel(hotelId);
    }

    // ---- customer (public) ----
    @GetMapping("/api/hotels/search")
    public List<HotelSearchResponse> search(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "1") int rooms) {
        return hotelService.search(city, checkIn, checkOut, rooms);
    }
}
