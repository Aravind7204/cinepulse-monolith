package com.cinepulse.modules.show.dto;

import com.cinepulse.modules.show.Show;
import java.time.LocalDateTime;

public record ShowResponse(
        Long id,
        Long movieId,
        String movieTitle,
        Long screenId,
        String screenName,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static ShowResponse fromEntity(Show show) {
        return new ShowResponse(
                show.getId(),
                show.getMovie().getId(),
                show.getMovie().getTitle(),
                show.getScreen().getId(),
                show.getScreen().getName(),
                show.getStartTime(),
                show.getEndTime()
        );
    }
}