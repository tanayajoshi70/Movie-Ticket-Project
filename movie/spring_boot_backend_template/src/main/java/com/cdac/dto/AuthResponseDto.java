package com.cdac.dto;

import lombok.*;


@Data
@AllArgsConstructor
public class AuthResponseDto {
   
	private String token;
    private String role;
    
    public AuthResponseDto(String token) {
		this.token=token;
	}
}
