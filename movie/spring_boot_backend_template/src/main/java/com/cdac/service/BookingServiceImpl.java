package com.cdac.service;

import com.cdac.custom_exception.ResourceNotFoundException;

import com.cdac.dto.AdminBookingRespDto;
import com.cdac.dto.BookedSeatsRespDto;
import com.cdac.dto.BookingByShowRespDto;
import com.cdac.dto.BookingCancelRespDto;
import com.cdac.dto.BookingFilterRespDto;
import com.cdac.dto.BookingReqDto;
import com.cdac.dto.BookingRespDto;
import com.cdac.dto.BookingStatusUpdateDto;
import com.cdac.dto.BookingUserReqDto;
import com.cdac.dto.NewBookRespDto;
import com.cdac.dto.SeatAvailableRespDto;
import com.cdac.entities.*;

import com.cdac.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Getter
@Setter
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepo;
    private final ShowRepository showRepo;
    private final SeatRepository seatRepo;
    private final BookingRepository bookingRepo;
    private final BookingSeatRepository bookingSeatRepo;
    private final PaymentRepository paymentRepo;
    private final ModelMapper modelMapper;
    

    @Transactional
    @Override
    public BookingRespDto bookSeats(BookingReqDto dto, String username) {
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Show show = showRepo.findById(dto.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        List<Seat> selectedSeats = seatRepo.findAllById(dto.getSeatIds());

        if (selectedSeats.stream().anyMatch(Seat::isBooked)) {
            throw new IllegalArgumentException("One or more seats are already booked");
        }

        double totalAmount = selectedSeats.stream().mapToDouble(Seat::getPrice).sum();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setTotalAmount(totalAmount);
        booking.setBookingTime(LocalDateTime.now());

        booking.setStatus("CONFIRMED");
        booking.setPaymentMode(dto.getPaymentMode());
        bookingRepo.save(booking);

        for (Seat seat : selectedSeats) {
            seat.setBooked(true);
            seatRepo.save(seat);

            BookingSeat bs = new BookingSeat();
            bs.setBooking(booking);
            bs.setSeat(seat);
            bs.setShow(show);
            bookingSeatRepo.save(bs);
        }

        BookingRespDto respDto = modelMapper.map(booking, BookingRespDto.class);
        respDto.setShowId(show.getShowId());
        respDto.setBookedSeats(  // <-- correct setter method
            selectedSeats.stream()
                .map(Seat::getSeatNo)
                .collect(Collectors.toList())
        );
        respDto.setTotalAmount(totalAmount);
		return respDto;

    }
    
    @Override
    public List<SeatAvailableRespDto> getBookedSeatsByBookingId(Long bookingId, String username) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getEmail().equals(username)) {
            throw new RuntimeException("Access denied: Not your booking");
        }

        List<BookingSeat> bookedSeats = bookingSeatRepo.findByBookingBookingId(bookingId);

        return bookedSeats.stream()
                .map(bs -> modelMapper.map(bs.getSeat(), SeatAvailableRespDto.class))
                .toList();
    }
    
    @Transactional
    @Override
    public BookingRespDto bookShow(BookingUserReqDto dto, String username) {
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Show show = showRepo.findById(dto.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        List<String> seatNos = dto.getSeatNos();
        if (seatNos == null || seatNos.isEmpty()) {
            throw new RuntimeException("Seat numbers must not be null or empty.");
        }

        List<Seat> selectedSeats = seatRepo.findByShowShowIdAndSeatNoIn(dto.getShowId(), seatNos);

        if (selectedSeats.isEmpty()) {
            throw new ResourceNotFoundException("Selected seats not found");
        }

        if (selectedSeats.stream().anyMatch(Seat::isBooked)) {
            throw new IllegalArgumentException("One or more seats are already booked");
        }

        double totalAmount = selectedSeats.stream().mapToDouble(Seat::getPrice).sum();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setBookingTime(LocalDateTime.now());
        booking.setPaymentMode(dto.getPaymentMode());
        booking.setTotalAmount(totalAmount);
        booking.setStatus("CONFIRMED");

        bookingRepo.save(booking);

        for (Seat seat : selectedSeats) {
            seat.setBooked(true);
            seatRepo.save(seat);

            BookingSeat bs = new BookingSeat();
            bs.setBooking(booking);
            bs.setSeat(seat);
            bs.setShow(show);
            bookingSeatRepo.save(bs);
        }
        
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentMode(dto.getPaymentMode());
        payment.setTotalAmount(totalAmount);
        payment.setStatus("SUCCESS");
        payment.setTime(LocalDateTime.now());

        paymentRepo.save(payment);

        BookingRespDto resp = modelMapper.map(booking, BookingRespDto.class);
        resp.setShowId(show.getShowId());
        resp.setBookedSeats(selectedSeats.stream().map(Seat::getSeatNo).collect(Collectors.toList()));
        resp.setTotalAmount(totalAmount);
        return resp;
    }
    
    @Transactional
    @Override
    public List<NewBookRespDto> getBookingsForUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepo.findByUser(user);

        return bookings.stream().map(b -> {
            NewBookRespDto dto = new NewBookRespDto();
            dto.setBookingId(b.getBookingId());
            dto.setBookingTime(b.getBookingTime());

            if (b.getPayment() != null) {
                dto.setPaymentMode(b.getPayment().getMethod());
                dto.setTotalAmount(b.getPayment().getAmount());
            }

            if (b.getShow() != null) {
                dto.setShowTitle(b.getShow().getMovie().getTitle());
            }

            dto.setBookedSeats(
                b.getBookingSeats().stream()
                    .map(seat -> seat.getSeat().getSeatNumber())
                    .collect(Collectors.toList())
            );

            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<BookedSeatsRespDto> getBookedSeatsForUserBooking(Long bookingId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized access to booking");
        }

        return booking.getBookingSeats().stream()
                .map(bs -> new BookedSeatsRespDto(bs.getSeat().getSeatNumber()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public BookingCancelRespDto cancelBooking(Long bookingId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You are not allowed to cancel this booking");
        }

        List<BookingSeat> bookingSeats = booking.getBookingSeats();
        List<String> seatNumbers = bookingSeats.stream()
                .map(bs -> bs.getSeat().getSeatNumber())
                .collect(Collectors.toList());

        // Mark seats as not booked
        bookingSeats.forEach(bs -> {
            bs.getSeat().setBooked(false);
            seatRepo.save(bs.getSeat());
        });

        bookingRepo.delete(booking); // Delete the booking

        return new BookingCancelRespDto(
                bookingId,
                "Booking cancelled successfully",
                LocalDateTime.now(),
                seatNumbers
        );
    }
    
    @Transactional(readOnly = true)
    @Override
    public List<AdminBookingRespDto> getAllBookingsForAdmin() {
        List<Booking> bookings = bookingRepo.findAllWithUserAndSeats();

        return bookings.stream().map(b -> {
            AdminBookingRespDto dto = new AdminBookingRespDto();
            dto.setBookingId(b.getBookingId());
            dto.setBookingTime(b.getBookingTime());

            // USER
            dto.setUserName(b.getUser().getName());
            dto.setUserEmail(b.getUser().getEmail());

            // SHOW
            dto.setShowTitle(b.getShow().getMovie().getTitle());
            dto.setTheaterName(b.getShow().getTheater().getName());
            dto.setStartTime(b.getShow().getStartTime());

            // PAYMENT (check null)
            if (b.getPayment() != null) {
                dto.setPaymentMode(b.getPayment().getPaymentMode());
                dto.setTotalAmount(b.getPayment().getTotalAmount());
            } else {
                dto.setPaymentMode("N/A");
                dto.setTotalAmount(0.0);
            }

            // SEATS
            dto.setBookedSeats(
                b.getBookingSeats()
                 .stream()
                 .map(bs -> bs.getSeat().getSeatNumber())
                 .collect(Collectors.toList())
            );

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingByShowRespDto> getBookingsByShow(Long showId) {
        List<Booking> bookings = bookingRepo.findByShowShowId(showId);

        return bookings.stream().map(booking -> {
            List<String> seats = bookingSeatRepo.findByBookingBookingId(booking.getBookingId())
                    .stream()
                    .map(bs -> bs.getSeat().getSeatNumber())
                    .collect(Collectors.toList());

            return new BookingByShowRespDto(
                    booking.getBookingId(),
                    booking.getBookingTime(),
                    booking.getPayment().getPaymentMode(),
                    booking.getPayment().getTotalAmount(),
                    booking.getPayment().getStatus(),
                    booking.getUser().getName(),
                    seats
            );
        }).collect(Collectors.toList());
    }
    
    @Transactional
    @Override
    public List<NewBookRespDto> getBookingsByUserId(Long userId) {
        List<Booking> bookings = bookingRepo.findByUserUserId(userId);

        return bookings.stream().map(booking -> {
            NewBookRespDto dto = new NewBookRespDto();
            dto.setBookingId(booking.getBookingId());
            dto.setBookingTime(booking.getBookingTime());
            dto.setShowTitle(booking.getShow().getMovie().getTitle());

            dto.setBookedSeats(
                booking.getBookingSeats()
                       .stream()
                       .map(bs -> bs.getSeat().getSeatNumber())
                       .collect(Collectors.toList())
            );

            if (booking.getPayment() != null) {
                dto.setPaymentMode(booking.getPayment().getPaymentMode());
                dto.setTotalAmount(booking.getPayment().getTotalAmount());
            } else {
                dto.setPaymentMode(null);
                dto.setTotalAmount(null);
            }

            return dto;
        }).collect(Collectors.toList());
    }
    
    @Transactional
    @Override
    public List<BookingFilterRespDto> getBookingsByDateRange(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(23, 59, 59);

        List<Booking> bookings = bookingRepo.findByBookingTimeBetween(start, end);

        return bookings.stream().map(b -> {
            BookingFilterRespDto dto = new BookingFilterRespDto();
            dto.setBookingId(b.getBookingId());
            dto.setBookingTime(b.getBookingTime());

            // ✅ Null-safe payment check
            if (b.getPayment() != null) {
                dto.setPaymentMode(b.getPayment().getPaymentMode());
                dto.setTotalAmount(b.getPayment().getTotalAmount());
            } else {
                dto.setPaymentMode("N/A");
                dto.setTotalAmount(0.0);
            }

            // ✅ Show title
            dto.setShowTitle(
                b.getShow() != null && b.getShow().getMovie() != null
                    ? b.getShow().getMovie().getTitle()
                    : "N/A"
            );

            // ✅ Theater name
            dto.setTheaterName(
                b.getShow() != null && b.getShow().getTheater() != null
                    ? b.getShow().getTheater().getName()
                    : "N/A"
            );

            // ✅ Use startTime instead of showTime
            if (b.getShow() != null && b.getShow().getStartTime() != null) {
                dto.setStartTime(b.getShow().getStartTime().toString());
            } else {
                dto.setStartTime("N/A");
            }

            // ✅ Booked seats
            dto.setBookedSeats(
                b.getBookingSeats() != null
                    ? b.getBookingSeats()
                        .stream()
                        .map(bs -> bs.getSeat().getSeatNumber())
                        .toList()
                    : List.of()
            );

            return dto;
        }).toList();
    }
    
    @Override
    public BookingRespDto updateBookingStatus(Long bookingId, BookingStatusUpdateDto dto) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        booking.setStatus(dto.getStatus());
        Booking updatedBooking = bookingRepo.save(booking);

        return modelMapper.map(updatedBooking, BookingRespDto.class);
    }



    
    
    


    
    


}

