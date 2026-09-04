package com.cinepulse.modules.booking;

import com.cinepulse.common.response.ApiResponse;
import com.cinepulse.modules.booking.dto.BookingResponse;
import com.cinepulse.modules.booking.dto.CreateBookingRequest;
import com.cinepulse.modules.booking.dto.PaymentResponse;
import com.cinepulse.modules.booking.dto.ProcessPaymentRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> reserveSeats(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.reserveSeats(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Seats reserved successfully. Proceed to payment."));
    }

    @PostMapping("/{bookingId}/pay")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @PathVariable Long bookingId,
            @Valid @RequestBody ProcessPaymentRequest request) {
        PaymentResponse payment = bookingService.confirmPayment(bookingId, request);
        return ResponseEntity.ok(ApiResponse.ok(payment, "Payment confirmed and booking finalized"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getBookingById(id)));
    }
}