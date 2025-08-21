package com.cdac.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.entities.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByBookingUserUserId(Long userId);
	
	 List<Payment> findByBooking_User_UserId(Long userId);
	 
	 
	 
	 @EntityGraph(attributePaths = {
			    "booking", 
			    "booking.show", 
			    "booking.show.movie", 
			    "booking.show.theater"
			})
			Optional<Payment> findByBookingBookingId(Long bookingId);


	 List<Payment> findByBookingUserEmail(String email);


	/*@EntityGraph(attributePaths = "bookingSeats")
	Optional<Payment> findByBooking_BookingId(Long bookingId);*/
}
