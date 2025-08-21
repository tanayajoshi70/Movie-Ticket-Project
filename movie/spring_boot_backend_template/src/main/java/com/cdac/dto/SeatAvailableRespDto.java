package com.cdac.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailableRespDto {
    private Long id;
    private String seatNo;
    private boolean isBooked;
    private Double price;
}
