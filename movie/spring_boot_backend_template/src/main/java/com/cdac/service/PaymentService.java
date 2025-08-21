package com.cdac.service;

import java.util.List;

import com.cdac.dto.NewPaymentRespDto;
import com.cdac.dto.PaymentReqDto;
import com.cdac.dto.PaymentRespDto;
import com.cdac.dto.RetryPaymentRequestDto;
import com.cdac.dto.RetryPaymentResponseDto;

public interface PaymentService {
	
	NewPaymentRespDto makePayment(PaymentReqDto dto); //user make payment

	List<PaymentRespDto> getPaymentsForLoggedInUser(); //give a payment for loggedin user
	
	 PaymentRespDto getPaymentByBookingId(Long bookingId); //get payment by user 
	 
	 RetryPaymentResponseDto retryPayment(RetryPaymentRequestDto dto, String userEmail);//retry payment if fails
	 
	 PaymentRespDto getPaymentByBookingId1(Long bookingId);//get payment details by admin
	 
	 List<PaymentRespDto> getPaymentsByUser(String username);//get specific details by admin by user id
}

