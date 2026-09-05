package com.cinepulse.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Builder.Default
    private Instant timestamp = Instant.now();

    private int status;
    private String message;
    private T data;

    // --- Methods for AuthController ---
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .status(201)
                .message(message)
                .data(data)
                .build();
    }

    // --- Methods for Booking, Theatre, Movie, and Show Controllers ---
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    // --- Methods for GlobalExceptionHandler ---
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status(500) // The actual HTTP status is usually set by the ResponseEntity
                .message(message)
                .build();
    }

    public static ApiResponse<Map<String, String>> error(String message, Map<String, String> data) {
        return ApiResponse.<Map<String, String>>builder()
                .status(400)
                .message(message)
                .data(data)
                .build();
    }
}