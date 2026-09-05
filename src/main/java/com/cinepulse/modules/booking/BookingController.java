package com.cinepulse.modules.booking;

import com.cinepulse.common.response.ApiResponse;
import com.cinepulse.config.security.SecurityUtils;
import com.cinepulse.modules.booking.dto.BookingResponse;
import com.cinepulse.modules.booking.dto.CreateBookingRequest;
import com.cinepulse.modules.booking.dto.PaymentResponse;
import com.cinepulse.modules.booking.dto.ProcessPaymentRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final SecurityUtils securityUtils;

    // Injected SecurityUtils via constructor
    public BookingController(BookingService bookingService, SecurityUtils securityUtils) {
        this.bookingService = bookingService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> reserveSeats(@Valid @RequestBody CreateBookingRequest request) {
        // 1. Securely fetch the user ID from the JWT context
        Long userId = securityUtils.getCurrentUserId();

        // 2. Pass it to the service
        BookingResponse response = bookingService.reserveSeats(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Seats reserved successfully. Proceed to payment."));
    }

    @PostMapping("/{bookingId}/pay")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @PathVariable Long bookingId,
            @Valid @RequestBody ProcessPaymentRequest request) {

        Long userId = securityUtils.getCurrentUserId();
        PaymentResponse payment = bookingService.confirmPayment(userId, bookingId, request);

        return ResponseEntity.ok(ApiResponse.ok(payment, "Payment confirmed and booking finalized"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getBookingById(userId, id)));
    }

    // --- New Booking Management Endpoints (CP-15) ---

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings() {
        Long userId = securityUtils.getCurrentUserId();
        List<BookingResponse> myBookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(ApiResponse.ok(myBookings, "Fetched user bookings successfully"));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        BookingResponse cancelledBooking = bookingService.cancelBooking(userId, id);
        return ResponseEntity.ok(ApiResponse.ok(cancelledBooking, "Booking cancelled and seats released successfully"));
    }
}