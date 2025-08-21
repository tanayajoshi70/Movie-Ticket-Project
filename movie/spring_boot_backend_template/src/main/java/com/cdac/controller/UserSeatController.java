package com.cdac.controller;

import com.cdac.dto.SeatUserRespDto;
import com.cdac.service.SeatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserSeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/user/shows/{showId}/seats/available")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<SeatUserRespDto>> getAvailableSeats(@PathVariable Long showId) {
        List<SeatUserRespDto> availableSeats = seatService.getAvailableSeatsByShowId(showId);
        return ResponseEntity.ok(availableSeats);
    }
    
}
