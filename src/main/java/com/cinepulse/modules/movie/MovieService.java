package com.cinepulse.modules.movie;

import com.cinepulse.common.exception.ResourceNotFoundException;
import com.cinepulse.modules.movie.dto.CreateMovieRequest;
import com.cinepulse.modules.movie.dto.MovieResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional
    public MovieResponse createMovie(CreateMovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.title())
                .description(request.description())
                .language(request.language())
                .genre(request.genre())
                .durationInMinutes(request.durationInMinutes())
                .releaseDate(request.releaseDate())
                .build();

        return MovieResponse.fromEntity(movieRepository.save(movie));
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(MovieResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return MovieResponse.fromEntity(movie);
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> getMoviesByLanguage(String language) {
        return movieRepository.findByLanguageIgnoreCase(language)
                .stream()
                .map(MovieResponse::fromEntity)
                .toList();
    }
}