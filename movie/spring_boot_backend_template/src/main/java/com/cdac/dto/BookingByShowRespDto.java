package com.cdac.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingByShowRespDto {
    private Long bookingId;
    private LocalDateTime bookingTime;
    private String paymentMode;
    private Double totalAmount;
    private String status;
    private String userName;
    private List<String> bookedSeats;
}
