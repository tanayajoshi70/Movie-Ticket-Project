package com.cdac.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewBookRespDto {
    private Long bookingId;
    private LocalDateTime bookingTime;
    private String paymentMode;
    private Double totalAmount;
    private String showTitle;
    private List<String> bookedSeats;
}
