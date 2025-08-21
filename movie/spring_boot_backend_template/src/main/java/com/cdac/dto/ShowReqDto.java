package com.cdac.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowReqDto {

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Theater ID is required")
    private Long theaterId;
    
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
    

    @NotNull(message = "Price per seat is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal pricePerSeat;
}
