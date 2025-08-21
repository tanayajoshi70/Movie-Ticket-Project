package com.cdac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.entities.BookingSeat;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

	List<BookingSeat> findByBookingBookingId(Long bookingId);

}
