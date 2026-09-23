package com.parkgrid.billing.dto;

import java.time.LocalDateTime;

public class BillingCalculationRequest {
    private Long bookingId;
    private Long userId;
    private String userEmail;
    private Long slotId;
    private Long zoneId;
    private String vehicleType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public BillingCalculationRequest() {}

    public BillingCalculationRequest(Long bookingId, Long userId, String userEmail, Long slotId, Long zoneId, String vehicleType, LocalDateTime startTime, LocalDateTime endTime) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.slotId = slotId;
        this.zoneId = zoneId;
        this.vehicleType = vehicleType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
