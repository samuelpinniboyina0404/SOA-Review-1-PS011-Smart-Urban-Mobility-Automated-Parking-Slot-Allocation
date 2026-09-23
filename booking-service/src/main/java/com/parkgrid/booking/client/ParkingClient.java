package com.parkgrid.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;

@FeignClient(name = "parking-service", url = "${parking.service.url:}")
public interface ParkingClient {

    @GetMapping("/api/parking/slots/{slotId}")
    Map<String, Object> getSlotById(@PathVariable("slotId") Long slotId);

    @PutMapping("/api/parking/slots/{slotId}/reserve")
    ResponseEntity<Map<String, Object>> reserveSlot(@PathVariable("slotId") Long slotId);

    @PutMapping("/api/parking/slots/{slotId}/release")
    ResponseEntity<Map<String, Object>> releaseSlot(@PathVariable("slotId") Long slotId);

    @PutMapping("/api/parking/slots/{slotId}/occupy")
    ResponseEntity<Map<String, Object>> occupySlot(@PathVariable("slotId") Long slotId);
}
