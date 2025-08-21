package com.cdac.service;

import com.cdac.custom_exception.ResourceNotFoundException;
import com.cdac.dto.NewPaymentRespDto;
import com.cdac.dto.PaymentReqDto;
import com.cdac.dto.PaymentRespDto;
import com.cdac.dto.RetryPaymentRequestDto;
import com.cdac.dto.RetryPaymentResponseDto;
import com.cdac.entities.Booking;
import com.cdac.entities.Payment;
import com.cdac.entities.Show;
import com.cdac.repository.BookingRepository;
import com.cdac.repository.PaymentRepository;

import com.cdac.entities.User;
import com.cdac.repository.UserRepository;
import com.cdac.security.JWTUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;
    private final JWTUtils jwtUtils;
    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;
    private final ModelMapper modelMapper;
    
    @Override
    public NewPaymentRespDto makePayment(PaymentReqDto dto) {
        Booking booking = bookingRepo.findById(dto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentMode(dto.getPaymentMode());
        payment.setTotalAmount(booking.getTotalAmount()); // 💡 system generated
        payment.setStatus("PAID");
        payment.setTime(LocalDateTime.now());

        Payment saved = paymentRepo.save(payment);

        NewPaymentRespDto resp = modelMapper.map(saved, NewPaymentRespDto.class);
        resp.setBookingReference(booking.getShow().getMovie().getTitle());

        return resp;
    }
    

    @Override
    public List<PaymentRespDto> getPaymentsForLoggedInUser() {
        String email = JWTUtils.getCurrentUserEmail();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Payment> payments = paymentRepo.findByBookingUserUserId(user.getUserId());

        return payments.stream().map(p -> {
            PaymentRespDto dto = new PaymentRespDto();
            dto.setPaymentId(p.getPaymentId());
            dto.setPaymentMode(p.getPaymentMode());
            dto.setTotalAmount(p.getTotalAmount());
            dto.setStatus(p.getStatus());
            dto.setTime(p.getTime());
            dto.setBookingId(p.getBooking().getBookingId());
            dto.setShowTitle(p.getBooking().getShow().getMovie().getTitle());
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public PaymentRespDto getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepo.findByBookingBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found for booking ID: " + bookingId));

        PaymentRespDto dto = new PaymentRespDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setPaymentMode(payment.getMethod());
        dto.setTotalAmount(payment.getTotalAmount());
        dto.setStatus(payment.getStatus());
        dto.setTime(payment.getTime());
        dto.setBookingId(payment.getBooking().getBookingId());

        Show show = payment.getBooking().getShow();
        dto.setShowTitle(show.getMovie().getTitle());
        dto.setTheaterName(show.getTheater().getName());
        //dto.setShowTime(show.getShowTime());

        //dto.setShowTime(show.getShowTime() != null ? show.getShowTime() : LocalDateTime.MIN);
        LocalDateTime time = show.getStartTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");
        dto.setShowTime(time != null ? time.format(formatter) : "Not Scheduled");
        return dto;
    }

    @Override
    @Transactional
    public RetryPaymentResponseDto retryPayment(RetryPaymentRequestDto dto, String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepo.findById(dto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied: Booking does not belong to user");
        }

        // ✅ Get the payment and check if it's failed
        Payment payment = booking.getPayment();

        if (!payment.getStatus().equalsIgnoreCase("FAILED")) {
            throw new RuntimeException("Only failed payments can be retried");  // ✅ Exception thrown here
        }

        // ✅ Continue updating the payment
        payment.setPaymentMode(dto.getNewPaymentMode());
        payment.setTime(LocalDateTime.now());
        payment.setStatus("PAID");
        paymentRepo.save(payment);

        // ✅ Prepare and return response
        RetryPaymentResponseDto resp = new RetryPaymentResponseDto();
        resp.setPaymentId(payment.getPaymentId());
        resp.setBookingId(booking.getBookingId());
        resp.setPaymentMode(payment.getPaymentMode());
        resp.setStatus(payment.getStatus());
        resp.setTime(payment.getTime());

        return resp;
    }
   
    @Override
    public PaymentRespDto getPaymentByBookingId1(Long bookingId) {
        Payment payment = paymentRepo.findByBookingBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found for booking ID: " + bookingId));

        PaymentRespDto dto = new PaymentRespDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setPaymentMode(payment.getMethod());
        dto.setTotalAmount(payment.getTotalAmount());
        dto.setStatus(payment.getStatus());
        dto.setTime(payment.getTime());
        dto.setBookingId(payment.getBooking().getBookingId());

        Show show = payment.getBooking().getShow();
        dto.setShowTitle(show.getMovie().getTitle());
        dto.setTheaterName(show.getTheater().getName());

        LocalDateTime time = show.getStartTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");
        dto.setShowTime(time != null ? time.format(formatter) : "Not Scheduled");

        return dto;
    }
    
    @Override
    public List<PaymentRespDto> getPaymentsByUser(String username) {
        List<Payment> payments = paymentRepo.findByBookingUserEmail(username);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

        return payments.stream().map(payment -> {
            PaymentRespDto dto = new PaymentRespDto();
            dto.setPaymentId(payment.getPaymentId());
            dto.setPaymentMode(payment.getMethod());
            dto.setTotalAmount(payment.getTotalAmount());
            dto.setStatus(payment.getStatus());
            dto.setTime(payment.getTime());
            dto.setBookingId(payment.getBooking().getBookingId());

            Show show = payment.getBooking().getShow();
            dto.setShowTitle(show.getMovie().getTitle());
            dto.setTheaterName(show.getTheater().getName());
            dto.setShowTime(show.getStartTime().format(formatter));

            return dto;
        }).collect(Collectors.toList());
    }

}

