package com.example.booking.user;

public record UserResponse(Long id, String name, String email, String phone, Role role) {}
