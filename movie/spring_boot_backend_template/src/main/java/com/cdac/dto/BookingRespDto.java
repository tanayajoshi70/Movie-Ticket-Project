package com.cdac.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class BookingRespDto {
    private Long bookingId;
    private Long showId;
    private List<String> bookedSeats;
    private double totalAmount;
    private String status;
    private String paymentMode;
    
	public void bookedSeats(List<String> collect) {
		this.bookedSeats = bookedSeats;
		
	}
	
	
	
    
}
