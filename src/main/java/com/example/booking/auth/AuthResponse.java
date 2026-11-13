package com.example.booking.auth;

import com.example.booking.user.Role;

public record AuthResponse(String token, String name, String email, Role role) {}
