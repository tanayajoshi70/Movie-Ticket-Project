package com.cdac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.dto.DeactivateSelfRequestDto;
import com.cdac.dto.UpdateUserPasswordRequestDTO;
import com.cdac.dto.UpdateUserProfileRequestDTO;
import com.cdac.dto.UserProfileDTO;
import com.cdac.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public UserProfileDTO getUserProfile(Authentication auth) {
        return userService.getCurrentUserProfile(auth);
    }
    
    @PutMapping("/update-profile")
    public ResponseEntity<String> updateProfile(Authentication auth,
                                                @RequestBody UpdateUserProfileRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUserProfile(auth, dto));
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(Authentication auth,
                                                 @RequestBody UpdateUserPasswordRequestDTO dto) {
        return ResponseEntity.ok(userService.updatePassword(auth, dto));
    }
    
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/deactivate-own")
    public ResponseEntity<String> deactivateOwnAccount(@RequestBody DeactivateSelfRequestDto dto) {
        String msg = userService.deactivateOwnAccount(dto.getEmail());
        return ResponseEntity.ok(msg);
    }

}



