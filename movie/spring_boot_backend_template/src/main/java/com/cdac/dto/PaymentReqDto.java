package com.cdac.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentReqDto {
	private String paymentMode; // UPI, CARD, etc.
    private Long bookingId;     // ID of the booking user is paying for
}
