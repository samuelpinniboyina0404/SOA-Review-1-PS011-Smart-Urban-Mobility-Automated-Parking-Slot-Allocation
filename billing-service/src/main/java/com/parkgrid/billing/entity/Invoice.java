package com.parkgrid.billing.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Long userId;

    private String userEmail;
    private Long slotId;
    private Long zoneId;
    private String vehicleType;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMinutes;

    private Double baseRatePerHour;
    private Double vehicleTypeMultiplier;
    private Double peakSurchargeMultiplier;
    private Double overstayPenalty;
    private Double totalAmount;

    @Column(nullable = false)
    private String paymentStatus; // PENDING, PAID

    private String paymentMethod;
    private LocalDateTime paidAt;
    private LocalDateTime issuedAt;

    public Invoice() {}

    public Invoice(String invoiceNumber, Long bookingId, Long userId, String userEmail, Long slotId, Long zoneId,
                   String vehicleType, LocalDateTime startTime, LocalDateTime endTime, Long durationMinutes,
                   Double baseRatePerHour, Double vehicleTypeMultiplier, Double peakSurchargeMultiplier,
                   Double overstayPenalty, Double totalAmount) {
        this.invoiceNumber = invoiceNumber;
        this.bookingId = bookingId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.slotId = slotId;
        this.zoneId = zoneId;
        this.vehicleType = vehicleType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.baseRatePerHour = baseRatePerHour;
        this.vehicleTypeMultiplier = vehicleTypeMultiplier;
        this.peakSurchargeMultiplier = peakSurchargeMultiplier;
        this.overstayPenalty = overstayPenalty;
        this.totalAmount = totalAmount;
        this.paymentStatus = "PENDING";
        this.issuedAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (issuedAt == null) issuedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

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

    public Long getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Long durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getBaseRatePerHour() { return baseRatePerHour; }
    public void setBaseRatePerHour(Double baseRatePerHour) { this.baseRatePerHour = baseRatePerHour; }

    public Double getVehicleTypeMultiplier() { return vehicleTypeMultiplier; }
    public void setVehicleTypeMultiplier(Double vehicleTypeMultiplier) { this.vehicleTypeMultiplier = vehicleTypeMultiplier; }

    public Double getPeakSurchargeMultiplier() { return peakSurchargeMultiplier; }
    public void setPeakSurchargeMultiplier(Double peakSurchargeMultiplier) { this.peakSurchargeMultiplier = peakSurchargeMultiplier; }

    public Double getOverstayPenalty() { return overstayPenalty; }
    public void setOverstayPenalty(Double overstayPenalty) { this.overstayPenalty = overstayPenalty; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
}
