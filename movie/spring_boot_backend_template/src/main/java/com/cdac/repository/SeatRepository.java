package com.cdac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.entities.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	
	List<Seat> findByShowShowId(Long showId);
	
	List<Seat> findByShowShowIdAndIsBookedFalse(Long showId);

	List<Seat> findByShowShowIdAndSeatNoIn(Long showId, List<String> seatNos);




}
