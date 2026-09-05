package com.cinepulse.modules.cinema;

import com.cinepulse.common.response.ApiResponse;
import com.cinepulse.modules.cinema.dto.CreateScreenRequest;
import com.cinepulse.modules.cinema.dto.CreateTheatreRequest;
import com.cinepulse.modules.cinema.dto.TheatreResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/theatres")
public class TheatreController {

    private final TheatreService theatreService;

    public TheatreController(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TheatreResponse>> createTheatre(@Valid @RequestBody CreateTheatreRequest request) {
        TheatreResponse response = theatreService.createTheatre(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Theatre created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TheatreResponse>>> getTheatresByCity(@RequestParam String city) {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.getTheatresByCity(city)));
    }

    @PostMapping("/{theatreId}/screens")
    public ResponseEntity<ApiResponse<Long>> addScreen(
            @PathVariable Long theatreId,
            @Valid @RequestBody CreateScreenRequest request) {
        Long screenId = theatreService.addScreenWithAutoSeats(theatreId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(screenId, "Screen and seat layout generated successfully"));
    }

    // --- New Admin Endpoints ---

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TheatreResponse>> updateTheatre(
            @PathVariable Long id,
            @Valid @RequestBody CreateTheatreRequest request) {
        TheatreResponse updatedTheatre = theatreService.updateTheatre(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updatedTheatre, "Theatre updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTheatre(@PathVariable Long id) {
        theatreService.deleteTheatre(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Theatre deleted successfully"));
    }
}