package com.cdac.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingUserRespDto {
    private Long bookingId;
    private Long showId;
    private Date bookingTime;
    private double totalAmount;
    private String status;
    private List<String> seatNos;
}
