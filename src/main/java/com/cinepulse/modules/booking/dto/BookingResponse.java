package com.cinepulse.modules.booking.dto;

import com.cinepulse.modules.booking.Booking;
import com.cinepulse.modules.booking.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long bookingId,
        String bookingReference,
        Long userId,
        Long showId,
        String movieTitle,
        BigDecimal totalAmount,
        BookingStatus status,
        List<Long> seatIds,
        LocalDateTime createdAt
) {
    public static BookingResponse fromEntity(Booking booking) {
        List<Long> seatIds = booking.getBookedSeats().stream()
                .map(seat -> seat.getId())
                .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getBookingReference(),
                booking.getUser().getId(),
                booking.getShow().getId(),
                booking.getShow().getMovie().getTitle(),
                booking.getTotalAmount(),
                booking.getStatus(),
                seatIds,
                booking.getCreatedAt()
        );
    }
}