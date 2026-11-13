package com.example.booking.auth;

import com.example.booking.common.exception.BadRequestException;
import com.example.booking.common.exception.ConflictException;
import com.example.booking.common.exception.UnauthorizedException;
import com.example.booking.user.Role;
import com.example.booking.user.User;
import com.example.booking.user.UserRepository;
import com.example.booking.vendor.Vendor;
import com.example.booking.vendor.VendorRepository;
import com.example.booking.vendor.VendorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        Role role = req.role() == null ? Role.CUSTOMER : req.role();
        if (role == Role.ADMIN) {
            throw new BadRequestException("You cannot register as ADMIN");
        }
        if (role == Role.VENDOR && (req.businessName() == null || req.businessName().isBlank()
                || req.vendorType() == null)) {
            throw new BadRequestException("businessName and vendorType are required for VENDOR");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("Email already registered");
        }

        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPhone(req.phone());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setRole(role);
        user = userRepository.save(user);

        if (role == Role.VENDOR) {
            Vendor vendor = new Vendor();
            vendor.setUser(user);
            vendor.setBusinessName(req.businessName());
            vendor.setGstNo(req.gstNo());
            vendor.setType(req.vendorType());
            vendor.setStatus(VendorStatus.PENDING);
            vendorRepository.save(vendor);
        }
        return toResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return toResponse(user);
    }

    private AuthResponse toResponse(User u) {
        return new AuthResponse(jwtUtil.generateToken(u.getEmail(), u.getRole().name()),
                u.getName(), u.getEmail(), u.getRole());
    }
}
