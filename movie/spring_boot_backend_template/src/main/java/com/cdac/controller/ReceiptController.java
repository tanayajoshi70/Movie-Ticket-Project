package com.cdac.controller;

import com.cdac.service.ReceiptService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{bookingId}")
    public void downloadReceipt(@PathVariable Long bookingId, HttpServletResponse response) {
        receiptService.downloadInvoice(bookingId, response);
    }
}
