package com.cdac.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.entities.Show;

public interface ShowRepository extends JpaRepository<Show, Long> {
	
	 List<Show> findByTheaterTheaterId(Long theaterId);
	
	 List<Show> findByMovieMovieId(Long movieId);
	 
	 List<Show> findByMovieTitleIgnoreCase(String title);
	 
	 List<Show> findByTheaterNameIgnoreCase(String theaterName);
	 
	 List<Show> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
	 
	 List<Show> findByStartTime(LocalDateTime startTime);

}
