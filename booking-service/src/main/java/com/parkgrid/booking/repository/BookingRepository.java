package com.parkgrid.booking.repository;

import com.parkgrid.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByStatus(String status);
    List<Booking> findBySlotIdAndStatusIn(Long slotId, List<String> statuses);
}
