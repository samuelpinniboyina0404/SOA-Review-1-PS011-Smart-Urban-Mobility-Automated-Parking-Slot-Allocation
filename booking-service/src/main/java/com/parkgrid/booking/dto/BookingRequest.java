package com.parkgrid.booking.dto;

public class BookingRequest {
    private Long userId;
    private String userEmail;
    private Long slotId;
    private String vehicleNumber;
    private String vehicleType;
    private Double expectedDurationHours;

    public BookingRequest() {}

    public BookingRequest(Long userId, String userEmail, Long slotId, String vehicleNumber, String vehicleType, Double expectedDurationHours) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.slotId = slotId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.expectedDurationHours = expectedDurationHours;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public Double getExpectedDurationHours() { return expectedDurationHours; }
    public void setExpectedDurationHours(Double expectedDurationHours) { this.expectedDurationHours = expectedDurationHours; }
}
