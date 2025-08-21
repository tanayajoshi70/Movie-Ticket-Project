package com.cdac.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingFilterRespDto {
    private Long bookingId;
    private LocalDateTime bookingTime;
    private String paymentMode;
    private Double totalAmount;
    private String showTitle;
    private String theaterName;
    private String startTime;
    private List<String> bookedSeats;
}
