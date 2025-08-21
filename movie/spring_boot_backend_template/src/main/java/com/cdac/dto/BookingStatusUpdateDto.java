package com.cdac.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingStatusUpdateDto {
    private String status; // e.g., CONFIRMED, CANCELLED, COMPLETED
}
