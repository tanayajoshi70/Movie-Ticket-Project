package com.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "theaters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Theater {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long theaterId;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, length = 100)
	private String location;

	@Column(nullable = false)
	private int totalSeats;
}
