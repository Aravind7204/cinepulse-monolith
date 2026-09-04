package com.cinepulse.modules.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByUserId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.createdAt < :cutoffTime")
    List<Booking> findExpiredPendingBookings(@Param("cutoffTime") LocalDateTime cutoffTime);
}