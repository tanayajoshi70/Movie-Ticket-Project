package com.cdac.controller;

import com.cdac.dto.NewPaymentRespDto;
import com.cdac.dto.PaymentReqDto;
import com.cdac.dto.PaymentRespDto;
import com.cdac.dto.RetryPaymentRequestDto;
import com.cdac.dto.RetryPaymentResponseDto;
import com.cdac.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user/payments")
@RequiredArgsConstructor
public class UserPaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<NewPaymentRespDto> makePayment(@RequestBody PaymentReqDto dto) {
        return ResponseEntity.ok(paymentService.makePayment(dto));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<PaymentRespDto>> viewMyPayments() {
        return ResponseEntity.ok(paymentService.getPaymentsForLoggedInUser());
    }
    
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentRespDto> getPaymentByBookingId(@PathVariable Long bookingId) {
        PaymentRespDto dto = paymentService.getPaymentByBookingId(bookingId);
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/retry")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RetryPaymentResponseDto> retryPayment(
            @RequestBody RetryPaymentRequestDto dto,
            Authentication auth) {
        String email = auth.getName();
        RetryPaymentResponseDto resp = paymentService.retryPayment(dto, email);
        return ResponseEntity.ok(resp);
    }
    
   
}
