package com.cinepulse.modules.cinema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateScreenRequest(
        @NotBlank(message = "Screen name is required")
        String name,
        @NotNull(message = "Total rows required")
        @Positive(message = "Total rows must be positive")
        Integer totalRows,
        @NotNull(message = "Seats per row required")
        @Positive(message = "Seats per row must be positive")
        Integer seatsPerRow
) {}