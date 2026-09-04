package com.cinepulse.modules.movie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record CreateMovieRequest(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotBlank(message = "Language is required")
        String language,
        @NotBlank(message = "Genre is required")
        String genre,
        @NotNull(message = "Duration is required")
        @Positive(message = "Duration must be positive")
        Integer durationInMinutes,
        LocalDate releaseDate
) {}