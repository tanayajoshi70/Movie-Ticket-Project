package com.cdac.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdac.entities.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {

	List<Movie> findByReleaseDateAfterOrReleaseDate(LocalDate dateAfter, LocalDate dateOn);

	
	List<Movie> findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseAndLanguageContainingIgnoreCase(
	        String title, String genre, String language);
	
	List<Movie> findByReleaseDateBeforeOrReleaseDateEquals(LocalDate before, LocalDate equals);
	
	List<Movie> findByReleaseDateBetween(LocalDate fromDate, LocalDate toDate);
	
	List<Movie> findByReleaseDateAfter(LocalDate date);



}
