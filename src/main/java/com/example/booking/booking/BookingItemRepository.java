package com.example.booking.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;

public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {

    /**
     * Given dates ke beech overlap karne wali active bookings me kitni quantity booked hai.
     * Overlap rule: existing.start < requested.end AND existing.end > requested.start
     */
    @Query("select coalesce(sum(i.quantity), 0) from BookingItem i " +
           "where i.booking.type = :type and i.itemRefId = :refId " +
           "and i.booking.status in :statuses " +
           "and i.startDate < :end and i.endDate > :start")
    long sumOverlap(@Param("type") BookingType type, @Param("refId") Long refId,
                    @Param("start") LocalDate start, @Param("end") LocalDate end,
                    @Param("statuses") Collection<BookingStatus> statuses);
}
