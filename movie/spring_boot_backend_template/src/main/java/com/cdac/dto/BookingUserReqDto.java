package com.cdac.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingUserReqDto {
    private Long showId;
    private List<String> seatNos;
    private String paymentMode;
    private double totalAmount;
}
