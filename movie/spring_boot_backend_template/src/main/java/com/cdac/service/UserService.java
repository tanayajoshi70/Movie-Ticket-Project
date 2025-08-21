package com.cdac.service;

import com.cdac.custom_exception.ResourceNotFoundException;
import com.cdac.dto.*;
import com.cdac.entities.*;
import com.cdac.repository.UserRepository;
import com.cdac.security.JWTUtils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(RegisterRequestDto request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setStatus(Status.ACTIVE);
        
        // ✅ ALLOW ONLY WHITELISTED EMAIL TO BE ADMIN
        if (request.getEmail().equalsIgnoreCase("admin@example.com")) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

        userRepository.save(user);
        return "User registered successfully";
    }

    public AuthResponseDto login(LoginRequestDto request) {
    	
    	    try {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
    	    }
    	    catch(Exception ex) {
    	    	 throw new RuntimeException("Invalid username or password");
    	    	
    	    }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponseDto(token, user.getRole().name());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }
    
    public UserProfileDTO getCurrentUserProfile(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfileDTO(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );

   }
    
    public String updateUserProfile(Authentication auth, UpdateUserProfileRequestDTO dto) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        userRepository.save(user);
        return "Profile updated successfully";
    }

   
    public String updatePassword(Authentication auth, UpdateUserPasswordRequestDTO dto) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return "Old password is incorrect";
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return "Password updated successfully";
    }
    
   
    public String deactivateUser(DeactivateUserRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == Status.INACTIVE) {
            return "User is already inactive.";
        }

        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
        return "User account deactivated successfully.";
    }
    
  
    public String deactivateOwnAccount(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() == Status.INACTIVE) {
            return "Account is already inactive.";
        }

        user.setStatus(Status.INACTIVE);
        userRepository.save(user);

        return "Your account has been deactivated successfully.";
    }
    
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserResponseDto(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRole(),
                        user.getStatus()
                )).collect(Collectors.toList());
    }
    
    
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        
        return dto;
    }



}