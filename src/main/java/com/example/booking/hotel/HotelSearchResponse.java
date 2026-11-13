package com.example.booking.hotel;

import java.util.List;

public record HotelSearchResponse(Long id, String name, String city, String address, String description,
                                  List<RoomResponse> rooms) {}
