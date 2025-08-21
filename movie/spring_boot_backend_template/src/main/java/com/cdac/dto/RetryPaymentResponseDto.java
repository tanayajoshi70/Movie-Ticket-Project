package com.cdac.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RetryPaymentResponseDto {
    private Long paymentId;
    private Long bookingId;
    private String paymentMode;
    private String status;
    private LocalDateTime time;
}
