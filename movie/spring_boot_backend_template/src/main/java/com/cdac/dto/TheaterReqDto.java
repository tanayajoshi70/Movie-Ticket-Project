package com.cdac.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TheaterReqDto {
    private String name;
    private String location;
    private int totalSeats;
}
