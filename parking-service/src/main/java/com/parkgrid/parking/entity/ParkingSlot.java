package com.parkgrid.parking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_slots")
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slotNumber;

    @Column(nullable = false)
    private Long zoneId;

    private Integer floorNumber;
    private String slotType; // STANDARD, EV_CHARGING, HANDICAPPED

    @Column(nullable = false)
    private String status; // AVAILABLE, RESERVED, OCCUPIED, MAINTENANCE

    private LocalDateTime lastUpdated;

    public ParkingSlot() {}

    public ParkingSlot(String slotNumber, Long zoneId, Integer floorNumber, String slotType, String status) {
        this.slotNumber = slotNumber;
        this.zoneId = zoneId;
        this.floorNumber = floorNumber;
        this.slotType = slotType;
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }

    public Integer getFloorNumber() { return floorNumber; }
    public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

    public String getSlotType() { return slotType; }
    public void setSlotType(String slotType) { this.slotType = slotType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
