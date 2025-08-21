package com.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Show {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long showId;

	@ManyToOne
	@JoinColumn(name = "movie_id", nullable = false)
	private Movie movie;

	@ManyToOne
	@JoinColumn(name = "theater_id", nullable = false)
	private Theater theater;

	@Column(nullable = false)
	private LocalDateTime startTime;

	@Column(nullable = false)
	private LocalDateTime endTime;

	@Column(nullable = false)
	private BigDecimal pricePerSeat;
	
	private String title;

	private LocalDateTime showTime;

	public String getTitle() {
	    return this.title;
	}

	public LocalDateTime getShowTime() {
        return showTime;
    }
	
	public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

}
	

