package com.parkgrid.parking.repository;

import com.parkgrid.parking.entity.ParkingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParkingZoneRepository extends JpaRepository<ParkingZone, Long> {
    List<ParkingZone> findByActiveTrue();
    List<ParkingZone> findByVehicleType(String vehicleType);
}
