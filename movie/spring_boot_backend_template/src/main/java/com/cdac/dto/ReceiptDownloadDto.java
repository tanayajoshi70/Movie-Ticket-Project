package com.cdac.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDownloadDto {
    private Long bookingId;
    private String userName;
    private String movieTitle;
    private String theaterName;
    private String showTime;
    private List<String> bookedSeats;
    private String paymentMode;
    private double totalAmount;
    private LocalDateTime bookingTime;
}
