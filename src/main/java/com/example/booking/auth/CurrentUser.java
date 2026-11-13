package com.example.booking.auth;

import com.example.booking.common.exception.ResourceNotFoundException;
import com.example.booking.common.exception.UnauthorizedException;
import com.example.booking.user.User;
import com.example.booking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Token se nikla logged-in user */
@Component
@RequiredArgsConstructor
public class CurrentUser {

    private final UserRepository userRepository;

    public User get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String email) || "anonymousUser".equals(email)) {
            throw new UnauthorizedException("Please login first");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
