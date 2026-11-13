package com.example.booking.bus;

public record BusResponse(Long id, String busNumber, BusType busType, int totalSeats) {}
