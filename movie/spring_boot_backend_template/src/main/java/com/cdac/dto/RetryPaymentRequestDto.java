package com.cdac.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetryPaymentRequestDto {
    private Long bookingId;
    private String newPaymentMode; // e.g., UPI, CARD
}
