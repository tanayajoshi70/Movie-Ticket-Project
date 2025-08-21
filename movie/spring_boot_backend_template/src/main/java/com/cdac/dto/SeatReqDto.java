package com.cdac.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatReqDto {
    private Long showId;
    private List<SeatData> seats;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatData {
        private String seatNo;
        private Double price;
        private boolean booked;  // New field
    }
}

