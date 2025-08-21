package com.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"seat_no", "show_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "seat_no", nullable = false, length = 10)
	private String seatNo;

	@Column(nullable = false)
	private boolean isBooked;

	@ManyToOne
	@JoinColumn(name = "show_id", nullable = false)
	private Show show;

	private Double price;
	
	public String getSeatNumber() {
	    return seatNo;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
}
