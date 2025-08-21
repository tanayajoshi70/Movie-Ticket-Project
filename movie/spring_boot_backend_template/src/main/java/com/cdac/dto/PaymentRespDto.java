package com.cdac.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentRespDto {

    private Long paymentId;
    private String paymentMode;
    private Double totalAmount;
    private String status;
    private LocalDateTime time;
    private Long bookingId;
    private String showTitle;
    private String theaterName;
    private String showTime;
}

