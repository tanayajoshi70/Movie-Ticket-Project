package com.cdac.controller;

import com.cdac.dto.PaymentRespDto;
import com.cdac.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentRespDto getPayment(@PathVariable Long bookingId) {
        return paymentService.getPaymentByBookingId1(bookingId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentRespDto> getMyPayments(Principal principal) {
        String username = principal.getName();
        return paymentService.getPaymentsByUser(username);
    }

}
