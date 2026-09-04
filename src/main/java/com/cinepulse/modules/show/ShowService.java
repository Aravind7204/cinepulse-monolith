package com.cinepulse.modules.show;

import com.cinepulse.common.exception.ConflictException;
import com.cinepulse.common.exception.ResourceNotFoundException;
import com.cinepulse.modules.cinema.Screen;
import com.cinepulse.modules.cinema.ScreenRepository;
import com.cinepulse.modules.cinema.Seat;
import com.cinepulse.modules.cinema.SeatRepository;
import com.cinepulse.modules.movie.Movie;
import com.cinepulse.modules.movie.MovieRepository;
import com.cinepulse.modules.show.dto.CreateShowRequest;
import com.cinepulse.modules.show.dto.ShowResponse;
import com.cinepulse.modules.show.dto.ShowSeatResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;

    public ShowService(ShowRepository showRepository,
                       ShowSeatRepository showSeatRepository,
                       MovieRepository movieRepository,
                       ScreenRepository screenRepository,
                       SeatRepository seatRepository) {
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.movieRepository = movieRepository;
        this.screenRepository = screenRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public ShowResponse createShow(CreateShowRequest request) {
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + request.movieId()));

        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + request.screenId()));

        // Calculate end time: duration + 15 min intermission/cleanup buffer
        LocalDateTime endTime = request.startTime().plusMinutes(movie.getDurationInMinutes() + 15);

        // Conflict check: Ensure screen has no overlapping shows
        List<Show> overlappingShows = showRepository.findByScreenIdAndStartTimeBetween(
                screen.getId(), request.startTime(), endTime
        );

        if (!overlappingShows.isEmpty()) {
            throw new ConflictException("Screen is already booked for another show during this time slot.");
        }

        Show show = Show.builder()
                .movie(movie)
                .screen(screen)
                .startTime(request.startTime())
                .endTime(endTime)
                .build();

        Show savedShow = showRepository.save(show);

        // Instantiate ShowSeat inventory from physical seats
        List<Seat> physicalSeats = seatRepository.findByScreenId(screen.getId());
        List<ShowSeat> showSeats = new ArrayList<>();

        for (Seat seat : physicalSeats) {
            BigDecimal price = switch (seat.getSeatType()) {
                case SILVER -> request.silverPrice();
                case GOLD -> request.goldPrice();
                case PLATINUM -> request.platinumPrice();
            };

            showSeats.add(ShowSeat.builder()
                    .show(savedShow)
                    .seat(seat)
                    .price(price)
                    .status(ShowSeatStatus.AVAILABLE)
                    .build());
        }

        showSeatRepository.saveAll(showSeats);
        return ShowResponse.fromEntity(savedShow);
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getShowsByMovie(Long movieId) {
        return showRepository.findByMovieId(movieId)
                .stream()
                .map(ShowResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShowSeatResponse> getShowSeatInventory(Long showId) {
        if (!showRepository.existsById(showId)) {
            throw new ResourceNotFoundException("Show not found with id: " + showId);
        }
        return showSeatRepository.findByShowId(showId)
                .stream()
                .map(ShowSeatResponse::fromEntity)
                .toList();
    }
}