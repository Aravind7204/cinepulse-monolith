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
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(@Valid @RequestBody CreateMovieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Movie created successfully", movieService.createMovie(request)));
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

    // --- New Admin Endpoints ---

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody CreateMovieRequest request) {
        MovieResponse updatedMovie = movieService.updateMovie(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updatedMovie, "Movie updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Movie deleted successfully"));
    }
}