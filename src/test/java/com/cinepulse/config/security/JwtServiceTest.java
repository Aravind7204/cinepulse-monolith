package com.cinepulse.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Standard 256-bit test secret
        String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        jwtService = new JwtService(secret, 3600000); // 1 hour expiration
        userDetails = new User("tester@cinepulse.com", "password", Collections.emptyList());
    }

    @Test
    @DisplayName("Should generate token and extract correct subject email")
    void generateToken_ShouldReturnValidTokenWithSubject() {
        String token = jwtService.generateToken(userDetails, Map.of("role", "CUSTOMER"));

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("tester@cinepulse.com");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("Should invalidate token if user details do not match subject")
    void isTokenValid_ShouldReturnFalse_WhenUsernameDiffers() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User("other@cinepulse.com", "password", Collections.emptyList());

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }
}