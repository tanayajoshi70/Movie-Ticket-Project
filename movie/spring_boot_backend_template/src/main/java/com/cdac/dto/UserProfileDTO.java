package com.cdac.dto;

import com.cdac.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileDTO {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Role role;
}
