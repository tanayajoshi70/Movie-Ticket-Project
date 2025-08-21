package com.cdac.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeactivateSelfRequestDto {
    @NotBlank
    @Email
    private String email;
}
