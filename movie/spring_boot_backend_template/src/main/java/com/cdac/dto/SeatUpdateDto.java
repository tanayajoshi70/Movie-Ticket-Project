package com.cdac.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatUpdateDto {
    private Double price;
    private Boolean booked;
}
