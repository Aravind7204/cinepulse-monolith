package com.cinepulse.modules.booking.dto;

import jakarta.validation.constraints.NotBlank;

public record ProcessPaymentRequest(
        @NotBlank(message = "Payment method is required (e.g. UPI, CARD, NETBANKING)")
        String paymentMethod
) {}