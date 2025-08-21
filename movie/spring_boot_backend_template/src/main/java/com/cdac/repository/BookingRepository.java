package com.cdac.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdac.entities.Booking;
import com.cdac.entities.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	

	List<Booking> findByUser(User user);
	
	@Query("SELECT b FROM Booking b " +
		       "JOIN FETCH b.user " +
		       "JOIN FETCH b.show s " +
		       "JOIN FETCH b.bookingSeats bs " +
		       "JOIN FETCH bs.seat " +
		       "LEFT JOIN FETCH b.payment")
		List<Booking> findAllWithUserAndSeats();

	List<Booking> findByShowShowId(Long showId);
	
	List<Booking> findByUserUserId(Long userId);
	
	 @Query("SELECT b FROM Booking b WHERE b.bookingTime BETWEEN :start AND :end")
	    List<Booking> findByBookingTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
