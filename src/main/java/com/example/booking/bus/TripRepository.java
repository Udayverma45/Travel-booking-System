package com.example.booking.bus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByBusVendorId(Long vendorId);

    @Query("select t from Trip t where lower(t.route.source) = lower(:source) " +
           "and lower(t.route.destination) = lower(:destination) " +
           "and t.departureTime >= :start and t.departureTime < :end and t.status = :status " +
           "order by t.departureTime")
    List<Trip> search(@Param("source") String source, @Param("destination") String destination,
                      @Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                      @Param("status") TripStatus status);
}
