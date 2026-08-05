package com.weatherapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "sensor_metadata")
public class SensorMetadata {

    @Id
    private String sensorId;

    private String location;

    private String gps;

    protected SensorMetadata() {
        // required by JPA
    }

    public SensorMetadata(String sensorId, String location, String gps) {
        this.sensorId = sensorId;
        this.location = location;
        this.gps = gps;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }
}
