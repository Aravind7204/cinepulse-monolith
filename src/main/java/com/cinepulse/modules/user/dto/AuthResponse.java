package com.cinepulse.modules.user.dto;

import com.cinepulse.modules.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private Role role;
}