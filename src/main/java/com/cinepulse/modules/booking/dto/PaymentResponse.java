package com.cinepulse.modules.booking.dto;

import com.cinepulse.modules.booking.Payment;
import com.cinepulse.modules.booking.PaymentStatus;
import java.math.BigDecimal;

public record PaymentResponse(
        Long paymentId,
        String transactionId,
        BigDecimal amount,
        PaymentStatus status,
        Long bookingId
) {
    public static PaymentResponse fromEntity(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getTransactionId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getBooking().getId()
        );
    }
}