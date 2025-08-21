package com.cdac.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class BookingReqDto {
    private Long showId;
    private List<Long> seatIds;
    private String paymentMode;
    private List<String> bookedSeats;	
	
}
