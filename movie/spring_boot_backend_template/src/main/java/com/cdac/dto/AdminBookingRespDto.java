package com.cdac.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminBookingRespDto {
    private Long bookingId;
    private String userName;
    private String userEmail;
    private String showTitle;
    private String theaterName;
    private LocalDateTime startTime;
    private Double totalAmount;
    private String paymentMode;
    private LocalDateTime bookingTime;
    private List<String> bookedSeats;
}
