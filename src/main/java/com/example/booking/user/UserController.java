package com.example.booking.user;

import com.example.booking.auth.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CurrentUser currentUser;

    @GetMapping("/me")
    public UserResponse me() {
        User u = currentUser.get();
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getRole());
    }
}
