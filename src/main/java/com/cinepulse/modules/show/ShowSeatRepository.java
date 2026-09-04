package com.cinepulse.modules.show;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    List<ShowSeat> findByShowId(Long showId);

    // Concurrency Lock: Prevents two concurrent transactions from selecting the same seat
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ss FROM ShowSeat ss WHERE ss.id IN :seatIds AND ss.status = 'AVAILABLE'")
    List<ShowSeat> findAvailableSeatsForUpdate(@Param("seatIds") List<Long> seatIds);
}