package com.cdac.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TheaterRespDto {
    private Long theaterId;
    private String name;
    private String location;
    private int totalSeats;
}
