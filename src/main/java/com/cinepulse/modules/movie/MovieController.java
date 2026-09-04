package com.cinepulse.modules.movie;

import com.cinepulse.common.response.ApiResponse;
import com.cinepulse.modules.movie.dto.CreateMovieRequest;
import com.cinepulse.modules.movie.dto.MovieResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponse>> addMovie(@Valid @RequestBody CreateMovieRequest request) {
        MovieResponse created = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "Movie registered successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getAllMovies() {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getAllMovies()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getMovieById(id)));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getMoviesByLanguage(@RequestParam String language) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getMoviesByLanguage(language)));
    }
}