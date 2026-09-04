package com.cinepulse.modules.booking;

import com.cinepulse.modules.show.ShowSeat;
import com.cinepulse.modules.show.ShowSeatRepository;
import com.cinepulse.modules.show.ShowSeatStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeatLockSweeperService {

    private static final Logger log = LoggerFactory.getLogger(SeatLockSweeperService.class);
    private static final int LOCK_TIMEOUT_MINUTES = 5;

    private final BookingRepository bookingRepository;
    private final ShowSeatRepository showSeatRepository;

    public SeatLockSweeperService(BookingRepository bookingRepository,
                                  ShowSeatRepository showSeatRepository) {
        this.bookingRepository = bookingRepository;
        this.showSeatRepository = showSeatRepository;
    }

    // Runs every 60 seconds (60,000 ms) with an initial 10-second delay
    @Scheduled(fixedRate = 60000, initialDelay = 10000)
    @Transactional
    public void sweepExpiredSeatLocks() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(LOCK_TIMEOUT_MINUTES);
        List<Booking> expiredBookings = bookingRepository.findExpiredPendingBookings(cutoffTime);

        if (expiredBookings.isEmpty()) {
            return;
        }

        log.info("Found {} expired booking reservations. Releasing seat locks...", expiredBookings.size());

        for (Booking booking : expiredBookings) {
            for (ShowSeat seat : booking.getBookedSeats()) {
                if (seat.getStatus() == ShowSeatStatus.LOCKED) {
                    seat.setStatus(ShowSeatStatus.AVAILABLE);
                    seat.setLockedAt(null);
                }
            }
            showSeatRepository.saveAll(booking.getBookedSeats());

            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);

            log.info("Expired reservation {} - seats returned to inventory.", booking.getBookingReference());
        }
    }
}