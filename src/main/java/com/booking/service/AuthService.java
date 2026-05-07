package com.booking.service;

import com.booking.dto.request.*;
import com.booking.dto.response.AuthResponse;
import com.booking.exception.BookingException;
import com.booking.model.*;
import com.booking.repository.UserRepository;
import com.booking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new BookingException("Username already taken");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new BookingException("Email already registered");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .country(request.getCountry())
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return buildResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BookingException("User not found"));
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .username(user.getUsername())
                .role(user.getRole().name())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .build();
    }
}
