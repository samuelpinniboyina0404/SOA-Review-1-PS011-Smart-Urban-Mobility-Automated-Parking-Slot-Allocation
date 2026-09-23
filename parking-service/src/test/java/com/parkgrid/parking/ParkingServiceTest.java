package com.parkgrid.parking;

import com.parkgrid.parking.entity.ParkingSlot;
import com.parkgrid.parking.entity.ParkingZone;
import com.parkgrid.parking.service.ParkingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "eureka.client.enabled=false")
public class ParkingServiceTest {

    @Autowired
    private ParkingService parkingService;

    @Test
    public void testZoneAndSlotInitialState() {
        List<ParkingZone> zones = parkingService.getAllZones();
        assertFalse(zones.isEmpty(), "Parking zones should be initialized");

        ParkingZone zoneA = zones.get(0);
        List<ParkingSlot> slots = parkingService.getSlotsByZone(zoneA.getId());
        assertFalse(slots.isEmpty(), "Parking slots should be created for zone A");
    }

    @Test
    public void testSlotReservationLifecycle() {
        List<ParkingSlot> availSlots = parkingService.getAvailableSlots(null, null);
        assertFalse(availSlots.isEmpty(), "Available slots should exist");

        ParkingSlot slot = availSlots.get(0);
        Long slotId = slot.getId();

        ParkingSlot reserved = parkingService.reserveSlot(slotId);
        assertEquals("RESERVED", reserved.getStatus());

        // Attempting to reserve an already reserved slot must throw IllegalStateException (Zero Double Booking)
        assertThrows(IllegalStateException.class, () -> parkingService.reserveSlot(slotId));

        ParkingSlot occupied = parkingService.occupySlot(slotId);
        assertEquals("OCCUPIED", occupied.getStatus());

        ParkingSlot released = parkingService.releaseSlot(slotId);
        assertEquals("AVAILABLE", released.getStatus());
    }

    @Test
    public void testOccupancyMetrics() {
        Map<String, Object> metrics = parkingService.getOccupancyMetrics();
        assertNotNull(metrics);
        assertTrue(metrics.containsKey("overallOccupancyPercentage"));
    }
}
