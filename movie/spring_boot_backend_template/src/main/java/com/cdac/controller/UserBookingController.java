package com.cdac.controller;

import com.cdac.dto.BookedSeatsRespDto;
import com.cdac.dto.BookingCancelRespDto;
import com.cdac.dto.BookingReqDto;
import com.cdac.dto.BookingRespDto;

import com.cdac.dto.BookingUserReqDto;
import com.cdac.dto.NewBookRespDto;
import com.cdac.dto.SeatAvailableRespDto;
import com.cdac.service.BookingService;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/bookings")
@AllArgsConstructor
public class UserBookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingRespDto> bookSeats(
            @RequestBody BookingReqDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        BookingRespDto response = bookingService.bookSeats(dto, username);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{bookingId}/seats")
    public List<SeatAvailableRespDto> getBookedSeats(@PathVariable Long bookingId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        return bookingService.getBookedSeatsByBookingId(bookingId, userDetails.getUsername());
    }
    
    @PostMapping("/shows")
    public ResponseEntity<BookingRespDto> bookShow(@RequestBody BookingUserReqDto bookingDto,
                                                   Authentication authentication) {
        String username = authentication.getName(); // from JWT token
        BookingRespDto booked = bookingService.bookShow(bookingDto, username);
        return ResponseEntity.ok(booked);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<NewBookRespDto> getMyBookings() {
        return bookingService.getBookingsForUser();
    }
    
    @GetMapping("booked/{bookingId}/seats")
    @PreAuthorize("hasRole('USER')")
    public List<BookedSeatsRespDto> getBookedSeats(@PathVariable Long bookingId) {
        return bookingService.getBookedSeatsForUserBooking(bookingId);
    }
    
    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingCancelRespDto> cancelBooking(@PathVariable Long bookingId) {
        BookingCancelRespDto response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(response);
    }
    
}
