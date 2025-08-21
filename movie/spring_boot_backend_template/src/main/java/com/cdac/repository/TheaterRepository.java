package com.cdac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.entities.Theater;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
	
	List<Theater> findByLocationIgnoreCase(String location);
	
	List<Theater> findByNameContainingIgnoreCase(String name);

}
