package com.parkgrid.booking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingReference;

    @Column(nullable = false)
    private Long userId;

    private String userEmail;

    @Column(nullable = false)
    private Long slotId;

    private Long zoneId;
    private String vehicleNumber;
    private String vehicleType;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;

    private Double expectedDurationHours;

    @Column(nullable = false)
    private String status; // CONFIRMED, ACTIVE, COMPLETED, CANCELLED

    private LocalDateTime createdAt;

    public Booking() {}

    public Booking(String bookingReference, Long userId, String userEmail, Long slotId, Long zoneId, 
                   String vehicleNumber, String vehicleType, Double expectedDurationHours) {
        this.bookingReference = bookingReference;
        this.userId = userId;
        this.userEmail = userEmail;
        this.slotId = slotId;
        this.zoneId = zoneId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.expectedDurationHours = expectedDurationHours;
        this.startTime = LocalDateTime.now();
        this.endTime = this.startTime.plusMinutes((long) (expectedDurationHours * 60));
        this.status = "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public LocalDateTime getActualCheckInTime() { return actualCheckInTime; }
    public void setActualCheckInTime(LocalDateTime actualCheckInTime) { this.actualCheckInTime = actualCheckInTime; }

    public LocalDateTime getActualCheckOutTime() { return actualCheckOutTime; }
    public void setActualCheckOutTime(LocalDateTime actualCheckOutTime) { this.actualCheckOutTime = actualCheckOutTime; }

    public Double getExpectedDurationHours() { return expectedDurationHours; }
    public void setExpectedDurationHours(Double expectedDurationHours) { this.expectedDurationHours = expectedDurationHours; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
