package com.cdac.controller;

import com.cdac.dto.*;
import com.cdac.entities.User;
import com.cdac.security.CustomUserDetailsService;
import com.cdac.security.JWTUtils;
import com.cdac.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	 @Autowired
	    private AuthenticationManager authenticationManager;

	    @Autowired
	    private JWTUtils jwtUtils;

	    @Autowired
	    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(userService.register(request));
    }

//    @PostMapping("/login")
//    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
//    	 System.out.println("Login API hit with email = " + request.getEmail());
//        return ResponseEntity.ok(userService.login(request));
//    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails.getUsername());
        
        User user = userService.getUserByEmail(userDetails.getUsername());
        
        return ResponseEntity.ok(new AuthResponseDto(token,user.getRole().name()));
    }
    
   
}
