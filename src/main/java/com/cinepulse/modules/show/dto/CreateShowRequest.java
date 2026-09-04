package com.cinepulse.modules.show.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateShowRequest(
        @NotNull(message = "Movie ID is required")
        Long movieId,

        @NotNull(message = "Screen ID is required")
        Long screenId,

        @NotNull(message = "Start time is required")
        @Future(message = "Show start time must be in the future")
        LocalDateTime startTime,

        @NotNull(message = "Base price is required")
        @Positive(message = "Base price must be greater than zero")
        BigDecimal silverPrice,

        @NotNull(message = "Gold price is required")
        @Positive(message = "Gold price must be greater than zero")
        BigDecimal goldPrice,

        @NotNull(message = "Platinum price is required")
        @Positive(message = "Platinum price must be greater than zero")
        BigDecimal platinumPrice
) {}