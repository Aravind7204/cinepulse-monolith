package com.cinepulse.modules.user.service;

import com.cinepulse.config.security.JwtService;
import com.cinepulse.config.security.SecurityUser;
import com.cinepulse.modules.user.User;
import com.cinepulse.modules.user.UserRepository;
import com.cinepulse.modules.user.dto.AuthResponse;
import com.cinepulse.modules.user.dto.LoginRequest;
import com.cinepulse.modules.user.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);
        SecurityUser securityUser = new SecurityUser(savedUser);

        String token = jwtService.generateToken(securityUser, Map.of("role", savedUser.getRole().name()));

        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), savedUser.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        SecurityUser securityUser = new SecurityUser(user);
        String token = jwtService.generateToken(securityUser, Map.of("role", user.getRole().name()));

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }
}