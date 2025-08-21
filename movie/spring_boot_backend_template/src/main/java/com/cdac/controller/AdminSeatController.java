package com.cdac.controller;

import com.cdac.dto.SeatReqDto;
import com.cdac.dto.SeatRespDto;
import com.cdac.dto.SeatUpdateDto;
import com.cdac.dto.SeatUserRespDto;
import com.cdac.service.SeatService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/seats")
@RequiredArgsConstructor
public class AdminSeatController {

    private final SeatService seatService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SeatRespDto>> addSeats(@RequestBody SeatReqDto dto) {
        List<SeatRespDto> added = seatService.addSeats(dto);
        return ResponseEntity.ok(added);
    }
    
    @PutMapping("/update/{seatId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeatRespDto> updateSeat(@PathVariable Long seatId,
                                                  @RequestBody SeatUpdateDto dto) {
        return ResponseEntity.ok(seatService.updateSeat(seatId, dto));
    }
    
    @DeleteMapping("/delete/{seatId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSeat(@PathVariable Long seatId) {
        seatService.deleteSeat(seatId);
        return ResponseEntity.ok("Seat deleted successfully");
    }
    
    @GetMapping("/shows/{showId}/seats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SeatRespDto>> getSeatsByShow(@PathVariable Long showId) {
        List<SeatRespDto> seats = seatService.getSeatsByShowId(showId);
        return ResponseEntity.ok(seats);
    }
    
    


}
