package com.weatherapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;


@Entity
@Table(
        name = "readings",
        indexes = {
                // Supports the main query access pattern: filter by sensor + metric + time range
                @Index(name = "idx_sensor_metric_ts", columnList = "sensorId, metric, timestamp")
        }
)
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sensorId;

    @Column(nullable = false)
    private String metric;

    @Column(name = "reading_value", nullable = false)
    private double value;

    @Column(nullable = false)
    private Instant timestamp;

    protected Reading() {
        // required by JPA
    }

    public Reading(String sensorId, String metric, double value, Instant timestamp) {
        this.sensorId = sensorId;
        this.metric = metric;
        this.value = value;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getMetric() {
        return metric;
    }

    public double getValue() {
        return value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
