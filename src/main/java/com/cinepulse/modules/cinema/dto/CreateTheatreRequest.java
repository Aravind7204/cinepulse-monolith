package com.cinepulse.modules.cinema.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTheatreRequest(
        @NotBlank(message = "Theatre name is required")
        String name,
        @NotBlank(message = "City is required")
        String city,
        @NotBlank(message = "Address is required")
        String address
) {}