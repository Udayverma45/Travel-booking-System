package com.example.booking.booking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "booking_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    /** BUS: trip_seat id, HOTEL: room id, RENTAL: vehicle id */
    private Long itemRefId;

    private String description;

    private int quantity = 1;

    /** Is item ka total price */
    private BigDecimal price;

    /** Hotel/rental ke liye dates (end exclusive) */
    private LocalDate startDate;
    private LocalDate endDate;
}
