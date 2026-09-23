package com.parkgrid.parking.repository;

import com.parkgrid.parking.entity.ParkingSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    List<ParkingSlot> findByZoneId(Long zoneId);

    List<ParkingSlot> findByZoneIdAndStatus(Long zoneId, String status);

    List<ParkingSlot> findByStatus(String status);

    long countByZoneIdAndStatus(Long zoneId, String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ParkingSlot s WHERE s.id = :id")
    Optional<ParkingSlot> findByIdForUpdate(@Param("id") Long id);
}
