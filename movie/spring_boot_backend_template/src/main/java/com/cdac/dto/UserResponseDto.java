package com.cdac.dto;

import com.cdac.entities.Role;
import com.cdac.entities.Status;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private Status status;
}
