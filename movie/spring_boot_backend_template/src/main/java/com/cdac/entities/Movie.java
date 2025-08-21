package com.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long movieId;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false)
	private LocalDate releaseDate;

	@Column(length = 10)
	private String rating;

	@Column(length = 30)
	private String language;

	@Column(length = 50)
	private String genre;

	@Column(length = 10)
	private String duration;
}
