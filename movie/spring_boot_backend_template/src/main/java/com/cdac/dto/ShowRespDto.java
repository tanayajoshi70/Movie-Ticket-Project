package com.cdac.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowRespDto {

    private Long showId;

    private String movieTitle;

    private String theaterName;

    private String theaterLocation;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal pricePerSeat;
}

