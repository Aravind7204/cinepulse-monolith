package com.cinepulse.modules.cinema;

import com.cinepulse.common.exception.ResourceNotFoundException;
import com.cinepulse.modules.cinema.dto.CreateScreenRequest;
import com.cinepulse.modules.cinema.dto.CreateTheatreRequest;
import com.cinepulse.modules.cinema.dto.TheatreResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TheatreService {

    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;

    public TheatreService(TheatreRepository theatreRepository, ScreenRepository screenRepository) {
        this.theatreRepository = theatreRepository;
        this.screenRepository = screenRepository;
    }

    @Transactional
    public TheatreResponse createTheatre(CreateTheatreRequest request) {
        Theatre theatre = Theatre.builder()
                .name(request.name())
                .city(request.city())
                .address(request.address())
                .build();

        return TheatreResponse.fromEntity(theatreRepository.save(theatre));
    }

    @Transactional(readOnly = true)
    public List<TheatreResponse> getTheatresByCity(String city) {
        return theatreRepository.findByCityIgnoreCase(city)
                .stream()
                .map(TheatreResponse::fromEntity)
                .toList();
    }

    @Transactional
    public Long addScreenWithAutoSeats(Long theatreId, CreateScreenRequest request) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + theatreId));

        int totalSeats = request.totalRows() * request.seatsPerRow();

        Screen screen = Screen.builder()
                .name(request.name())
                .totalSeats(totalSeats)
                .theatre(theatre)
                .build();

        List<Seat> seats = new ArrayList<>();
        char rowLetter = 'A';

        for (int r = 0; r < request.totalRows(); r++) {
            String rowStr = String.valueOf((char) (rowLetter + r));
            SeatType type = (r < 2) ? SeatType.SILVER : (r < 5) ? SeatType.GOLD : SeatType.PLATINUM;

            for (int s = 1; s <= request.seatsPerRow(); s++) {
                seats.add(Seat.builder()
                        .row(rowStr)
                        .seatNumber(s)
                        .seatType(type)
                        .screen(screen)
                        .build());
            }
        }

        screen.setSeats(seats);
        return screenRepository.save(screen).getId();
    }
}