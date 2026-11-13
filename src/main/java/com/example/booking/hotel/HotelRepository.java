package com.example.booking.hotel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByVendorId(Long vendorId);
    List<Hotel> findByCityIgnoreCase(String city);
}
