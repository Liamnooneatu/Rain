/*
 * represents the stored metadata information associated with a weather sensor.
 *
 * It contains:
 * - sensorId: A unique identifier for the sensor and the primary key of
 *   the database table.
 * - location: The physical location where the sensor is installed.
 * - gps: The GPS coordinates associated with the sensor location.
 *
 * The @Entity annotation maps this class to the "sensor_metadata" database
 * table, allowing sensor details to be persisted and retrieved using JPA.
 *
 * The protected no-argument constructor is required by JPA when creating
 * entity objects from database records.
 *
 * Setter methods are provided for location and GPS fields to allow sensor
 * metadata to be updated after creation.
 */


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
