package com.cdac.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ReceiptService {

	void downloadInvoice(Long bookingId, HttpServletResponse response);
}
