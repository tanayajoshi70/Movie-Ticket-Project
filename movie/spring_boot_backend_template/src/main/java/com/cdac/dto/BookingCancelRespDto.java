package com.cdac.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingCancelRespDto {
    private Long bookingId;
    private String message;
    private LocalDateTime cancelTime;
    private List<String> cancelledSeats;
}
