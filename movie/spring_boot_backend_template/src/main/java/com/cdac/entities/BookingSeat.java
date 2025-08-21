package com.cdac.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "booking_seats",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"booking_id", "seat_id", "show_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(optional = false)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "show_id")
    private Show show;
}
