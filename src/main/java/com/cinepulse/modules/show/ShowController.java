package com.cinepulse.modules.show;

import com.cinepulse.common.response.ApiResponse;
import com.cinepulse.modules.show.dto.CreateShowRequest;
import com.cinepulse.modules.show.dto.ShowResponse;
import com.cinepulse.modules.show.dto.ShowSeatResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShowResponse>> createShow(@Valid @RequestBody CreateShowRequest request) {
        ShowResponse response = showService.createShow(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Show scheduled and seat inventory initialized successfully"));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getShowsByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.ok(showService.getShowsByMovie(movieId)));
    }

    @GetMapping("/{showId}/seats")
    public ResponseEntity<ApiResponse<List<ShowSeatResponse>>> getShowSeats(@PathVariable Long showId) {
        return ResponseEntity.ok(ApiResponse.ok(showService.getShowSeatInventory(showId)));
    }
}