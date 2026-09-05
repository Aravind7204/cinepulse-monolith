package com.cinepulse.modules.booking;

import com.cinepulse.common.exception.BadRequestException;
import com.cinepulse.common.exception.ConflictException;
import com.cinepulse.common.exception.ResourceNotFoundException;
import com.cinepulse.modules.booking.dto.BookingResponse;
import com.cinepulse.modules.booking.dto.CreateBookingRequest;
import com.cinepulse.modules.booking.dto.PaymentResponse;
import com.cinepulse.modules.booking.dto.ProcessPaymentRequest;
import com.cinepulse.modules.show.Show;
import com.cinepulse.modules.show.ShowRepository;
import com.cinepulse.modules.show.ShowSeat;
import com.cinepulse.modules.show.ShowSeatRepository;
import com.cinepulse.modules.show.ShowSeatStatus;
import com.cinepulse.modules.user.User;
import com.cinepulse.modules.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ShowSeatRepository showSeatRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          PaymentRepository paymentRepository,
                          ShowSeatRepository showSeatRepository,
                          ShowRepository showRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.showSeatRepository = showSeatRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponse reserveSeats(Long userId, CreateBookingRequest request) {
        // userId is now securely provided by the JWT Context, not the client payload
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Show show = showRepository.findById(request.showId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + request.showId()));

        // Acquire pessimistic write lock on selected seats to prevent race conditions
        List<ShowSeat> selectedSeats = showSeatRepository.findAvailableSeatsForUpdate(request.showSeatIds());

        if (selectedSeats.size() != request.showSeatIds().size()) {
            throw new ConflictException("One or more selected seats are no longer available or do not exist.");
        }

        // Verify all seats belong to the requested show
        for (ShowSeat seat : selectedSeats) {
            if (!seat.getShow().getId().equals(show.getId())) {
                throw new BadRequestException("Seat ID " + seat.getId() + " does not belong to show ID " + show.getId());
            }
        }

        // Lock seats temporarily for payment
        BigDecimal totalAmount = BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();

        for (ShowSeat seat : selectedSeats) {
            seat.setStatus(ShowSeatStatus.LOCKED);
            seat.setLockedAt(now);
            totalAmount = totalAmount.add(seat.getPrice());
        }
        showSeatRepository.saveAll(selectedSeats);

        // Generate preliminary booking in PENDING state
        Booking booking = Booking.builder()
                .bookingReference("CP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .show(show)
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING)
                .bookedSeats(selectedSeats)
                .build();

        return BookingResponse.fromEntity(bookingRepository.save(booking));
    }

    @Transactional
    public PaymentResponse confirmPayment(Long userId, Long bookingId, ProcessPaymentRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // SECURITY CHECK: Prevent users from paying for someone else's booking
        if (!booking.getUser().getId().equals(userId)) {
            throw new ConflictException("You are not authorized to modify this booking.");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Booking cannot be paid. Current status: " + booking.getStatus());
        }

        // Finalize seat states to BOOKED
        for (ShowSeat seat : booking.getBookedSeats()) {
            seat.setStatus(ShowSeatStatus.BOOKED);
            seat.setLockedAt(null);
        }
        showSeatRepository.saveAll(booking.getBookedSeats());

        // Update booking state
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Record successful transaction
        Payment payment = Payment.builder()
                .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .amount(booking.getTotalAmount())
                .status(PaymentStatus.SUCCESS)
                .booking(booking)
                .build();

        return PaymentResponse.fromEntity(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long userId, Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // SECURITY CHECK: Prevent users from viewing someone else's booking
        if (!booking.getUser().getId().equals(userId)) {
            throw new ConflictException("You are not authorized to view this booking.");
        }

        return BookingResponse.fromEntity(booking);
    }
    // --- New Booking Management Methods (CP-15) ---

    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    @Transactional
    public BookingResponse cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // SECURITY CHECK: Ensure the user actually owns this booking
        if (!booking.getUser().getId().equals(userId)) {
            throw new ConflictException("You are not authorized to cancel this booking.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.EXPIRED) {
            throw new ConflictException("Booking is already " + booking.getStatus());
        }

        // Release the seats back to the public pool
        for (ShowSeat seat : booking.getBookedSeats()) {
            seat.setStatus(ShowSeatStatus.AVAILABLE);
            seat.setLockedAt(null);
        }
        showSeatRepository.saveAll(booking.getBookedSeats());

        // Update the booking status
        booking.setStatus(BookingStatus.CANCELLED);

        return BookingResponse.fromEntity(bookingRepository.save(booking));
    }
}