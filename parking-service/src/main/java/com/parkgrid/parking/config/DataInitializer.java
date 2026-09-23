package com.parkgrid.parking.config;

import com.parkgrid.parking.entity.ParkingSlot;
import com.parkgrid.parking.entity.ParkingZone;
import com.parkgrid.parking.repository.ParkingSlotRepository;
import com.parkgrid.parking.repository.ParkingZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ParkingZoneRepository zoneRepository;

    @Autowired
    private ParkingSlotRepository slotRepository;

    @Override
    public void run(String... args) throws Exception {
        if (zoneRepository.count() == 0) {
            // Zone 1: Tech Park Zone A
            ParkingZone zoneA = zoneRepository.save(new ParkingZone("Tech Park Zone A", "Level 1 - Main Concourse", 10, 5.0, "CAR"));
            createSlotsForZone(zoneA.getId(), "A", 1, 10, "STANDARD", "CAR");

            // Zone 2: GreenEV Hub
            ParkingZone zoneEV = zoneRepository.save(new ParkingZone("GreenEV Charging Hub", "Level 1 - East Wing", 8, 7.5, "EV"));
            createSlotsForZone(zoneEV.getId(), "EV", 1, 8, "EV_CHARGING", "EV");

            // Zone 3: Metro Transit Plaza
            ParkingZone zoneMetro = zoneRepository.save(new ParkingZone("Metro Transit Plaza", "Level 2 - South Deck", 10, 4.0, "BIKE"));
            createSlotsForZone(zoneMetro.getId(), "M", 2, 10, "STANDARD", "BIKE");

            // Zone 4: VIP Executive Bay
            ParkingZone zoneVIP = zoneRepository.save(new ParkingZone("VIP Executive Bay", "Basement B1 - Reserved", 6, 12.0, "SUV"));
            createSlotsForZone(zoneVIP.getId(), "VIP", 0, 6, "HANDICAPPED", "SUV");

            System.out.println(">>> Seeded 4 Parking Zones and 34 Parking Slots successfully!");
        }
    }

    private void createSlotsForZone(Long zoneId, String prefix, int floor, int count, String slotType, String defaultVehicle) {
        for (int i = 1; i <= count; i++) {
            String slotNum = String.format("%s-%d%02d", prefix, floor == 0 ? 1 : floor, i);
            String status = (i == count) ? "RESERVED" : "AVAILABLE";
            slotRepository.save(new ParkingSlot(slotNum, zoneId, floor, slotType, status));
        }
    }
}
