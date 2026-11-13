package com.example.booking.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByVendorIdOrderByCreatedAtDesc(Long vendorId);

    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime time);

    long countByVendorId(Long vendorId);

    long countByVendorIdAndStatus(Long vendorId, BookingStatus status);

    long countByStatus(BookingStatus status);

    @Query("select coalesce(sum(b.totalAmount), 0) from Booking b where b.vendorId = :vendorId and b.status = :status")
    BigDecimal revenueOfVendor(@Param("vendorId") Long vendorId, @Param("status") BookingStatus status);

    @Query("select coalesce(sum(b.totalAmount), 0) from Booking b where b.status = :status")
    BigDecimal totalRevenue(@Param("status") BookingStatus status);
}
