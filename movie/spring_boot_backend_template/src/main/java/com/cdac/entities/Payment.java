package com.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long paymentId;

	    
	    @Column(name = "method")
	    private String paymentMode; // 👈 Correctly mapped

	   
	    private String status;

	    private LocalDateTime time;

	    @Column(name = "total_amount")
	    private Double totalAmount; // 👈 Needs mapping

	    @OneToOne
	    @JoinColumn(name = "booking_id", referencedColumnName = "booking_id")
	    private Booking booking;
	    
	   
	    
	    public String getMethod() {
	        return paymentMode;
	    }

	    public Double getAmount() {
	        return totalAmount;
	    }

	 // ✅ Add these two setters only — to fix your error
	    public void setMethod(String method) {
	        this.paymentMode = method;
	    }

	    public void setAmount(Double amount) {
	        this.totalAmount = amount;
	    }
	}



