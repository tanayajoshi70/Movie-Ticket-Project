package com.cdac.controller;


import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.dto.AdminBookingRespDto;
import com.cdac.dto.BookingByShowRespDto;
import com.cdac.dto.BookingFilterRespDto;
import com.cdac.dto.BookingRespDto;
import com.cdac.dto.BookingStatusUpdateDto;
import com.cdac.dto.NewBookRespDto;
import com.cdac.service.BookingService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/admin/bookings")
@AllArgsConstructor
	public class AdminBookingController {

	    private final BookingService bookingService;

	    @GetMapping
	    @PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<List<AdminBookingRespDto>> getAllBookings() {
	        return ResponseEntity.ok(bookingService.getAllBookingsForAdmin());
	    }
	    
	    @GetMapping("/show/{showId}")
	    @PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<List<BookingByShowRespDto>> getBookingsByShow(@PathVariable Long showId) {
	        return ResponseEntity.ok(bookingService.getBookingsByShow(showId));
	    }
	    
	    @PreAuthorize("hasRole('ADMIN')")
	    @GetMapping("/user/{userId}")
	    public ResponseEntity<List<NewBookRespDto>> getBookingsByUser(@PathVariable Long userId) {
	        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
	    }

	    @GetMapping("/date-range")
	    @PreAuthorize("hasRole('ADMIN')")
	    public List<BookingFilterRespDto> getBookingsByDateRange(
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
	        return bookingService.getBookingsByDateRange(fromDate, toDate);
	    }
	    
	    @PutMapping("/{bookingId}/status")
	    @PreAuthorize("hasRole('ADMIN')")
	    public ResponseEntity<BookingRespDto> updateBookingStatus(
	            @PathVariable Long bookingId,
	            @RequestBody BookingStatusUpdateDto dto) {
	        return ResponseEntity.ok(bookingService.updateBookingStatus(bookingId, dto));
	    }
	    
}


