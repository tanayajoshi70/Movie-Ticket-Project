package com.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.cdac.entities.Show;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booking_id")
	private Long bookingId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.EAGER) 
	@JoinColumn(name = "show_id", nullable = false)
	private Show show;
	
	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<BookingSeat> bookingSeats;


	@Column(name = "booking_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime bookingTime;

	@OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
	private Payment payment;

	@Column(nullable = false)
	private double totalAmount;

	@Column(nullable = false)
	private String status;

	private String paymentMode;

	public void setPaymentMode(String paymentMode) {
		 this.paymentMode = paymentMode;
		
	}
	public String getPaymentMode() {
	    return paymentMode;
	}
	
	public void setTotalAmount(double total) {
		this.totalAmount = total;
	}
	
	@PrePersist
	public void prePersist() {
	    if (this.status == null) {
	        this.status = "PENDING"; // Or whatever your default should be
	    }
	}
	
	public double getTotalAmount() {
	    if (bookingSeats == null || bookingSeats.isEmpty()) return 0.0;

	    return bookingSeats.stream()
	                       .mapToDouble(bs -> bs.getSeat().getPrice())
	                       .sum();
	}
	public Payment getPayment() {
		return payment;
	}
	
	



	
}
