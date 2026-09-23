package com.parkgrid.parking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_zones")
public class ParkingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String location;
    private Integer totalCapacity;
    private Integer availableCapacity;
    private Double hourlyRate;
    private String vehicleType; // CAR, EV, BIKE, SUV
    private Boolean active = true;

    public ParkingZone() {}

    public ParkingZone(String name, String location, Integer totalCapacity, Double hourlyRate, String vehicleType) {
        this.name = name;
        this.location = location;
        this.totalCapacity = totalCapacity;
        this.availableCapacity = totalCapacity;
        this.hourlyRate = hourlyRate;
        this.vehicleType = vehicleType;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Integer totalCapacity) { this.totalCapacity = totalCapacity; }

    public Integer getAvailableCapacity() { return availableCapacity; }
    public void setAvailableCapacity(Integer availableCapacity) { this.availableCapacity = availableCapacity; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
