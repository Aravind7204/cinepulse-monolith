package com.cinepulse.modules.cinema.dto;

import com.cinepulse.modules.cinema.Theatre;

public record TheatreResponse(
        Long id,
        String name,
        String city,
        String address
) {
    public static TheatreResponse fromEntity(Theatre theatre) {
        return new TheatreResponse(
                theatre.getId(),
                theatre.getName(),
                theatre.getCity(),
                theatre.getAddress()
        );
    }
}