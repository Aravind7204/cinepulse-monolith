package com.cinepulse.modules.show.dto;

import com.cinepulse.modules.cinema.SeatType;
import com.cinepulse.modules.show.ShowSeat;
import com.cinepulse.modules.show.ShowSeatStatus;
import java.math.BigDecimal;

public record ShowSeatResponse(
        Long id,
        String row,
        Integer seatNumber,
        SeatType seatType,
        BigDecimal price,
        ShowSeatStatus status
) {
    public static ShowSeatResponse fromEntity(ShowSeat showSeat) {
        return new ShowSeatResponse(
                showSeat.getId(),
                showSeat.getSeat().getRow(),
                showSeat.getSeat().getSeatNumber(),
                showSeat.getSeat().getSeatType(),
                showSeat.getPrice(),
                showSeat.getStatus()
        );
    }
}