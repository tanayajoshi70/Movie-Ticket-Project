package com.cdac.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewPaymentRespDto {
    private Long paymentId;
    private String paymentMode;
    private Double totalAmount;
    private LocalDateTime time;
    private String bookingReference; // You can show booking ID or show title
}
