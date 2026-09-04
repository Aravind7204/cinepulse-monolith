package com.cinepulse.modules.movie.dto;

import com.cinepulse.modules.movie.Movie;
import java.time.LocalDate;

public record MovieResponse(
        Long id,
        String title,
        String description,
        String language,
        String genre,
        Integer durationInMinutes,
        LocalDate releaseDate
) {
    public static MovieResponse fromEntity(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getLanguage(),
                movie.getGenre(),
                movie.getDurationInMinutes(),
                movie.getReleaseDate()
        );
    }
}