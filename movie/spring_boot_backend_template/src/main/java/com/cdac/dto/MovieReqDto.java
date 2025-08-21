package com.cdac.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieReqDto {
    private String title;
    private LocalDate releaseDate;
    private String rating;
    private String language;
    private String genre;
    private String duration;
}
