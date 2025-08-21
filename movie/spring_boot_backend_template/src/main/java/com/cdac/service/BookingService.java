package com.cdac.service;

import java.time.LocalDate;
import java.util.List;


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

public interface BookingService {
    BookingRespDto bookSeats(BookingReqDto dto, String username); //seat booked by user
    
    List<SeatAvailableRespDto> getBookedSeatsByBookingId(Long bookingId, String username); //get available seats by user
    
    BookingRespDto bookShow(BookingUserReqDto dto, String username);//book a show by user
    
    List<NewBookRespDto> getBookingsForUser(); //view all bookings for user
    
    List<BookedSeatsRespDto> getBookedSeatsForUserBooking(Long bookingId); //get booked seats for user by user id
    
    BookingCancelRespDto cancelBooking(Long bookingId); //cancel ticket by user
    
    List<AdminBookingRespDto> getAllBookingsForAdmin();//get all bookings for admin
    
    List<BookingByShowRespDto> getBookingsByShow(Long showId);//get bookings by show by admin
    
    List<NewBookRespDto> getBookingsByUserId(Long userId);//get bookings by user id by admin
    
    List<BookingFilterRespDto> getBookingsByDateRange(LocalDate fromDate, LocalDate toDate);//get bookings by admin by date
    
    BookingRespDto updateBookingStatus(Long bookingId, BookingStatusUpdateDto dto);//update status by admin(pending,cancelled,completed)
    
   
    
}
