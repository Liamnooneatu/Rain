/*
 * represents the data received when a new weather sensor reading is submitted to the API.
 *
 * It contains:
 * - metric: The type of measurement being recorded (e.g., temperature, humidity, or wind speed).
 * - value: The numerical value of the sensor measurement.
 * - timestamp: The time when the reading was recorded.
 * - location: The physical location associated with the sensor reading.
 * - gps: GPS coordinates of the sensor location.
 *
 * Validation annotations are used to ensure required fields contain valid
 * data before the reading is processed:
 * - metric cannot be empty or blank and value cannot be null.
 *
 * separates incoming API request data from the internal layer and
 * allows Spring Boot to automatically map JSON request data into a Java object.
 */


package com.weatherapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class ReadingRequest {

    @NotBlank(message = "metric must not be blank")
    private String metric;

    @NotNull(message = "value must not be null")
    private Double value;

    private Instant timestamp;

    private String location;

    private String gps;

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

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
