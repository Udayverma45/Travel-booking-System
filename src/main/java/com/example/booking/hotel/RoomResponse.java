package com.example.booking.hotel;

import java.math.BigDecimal;

/** availableRooms sirf search me bhara hota hai, warna null */
public record RoomResponse(Long id, String roomType, BigDecimal pricePerNight, int totalRooms, Integer availableRooms) {}
