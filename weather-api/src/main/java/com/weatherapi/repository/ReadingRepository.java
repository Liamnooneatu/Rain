/*
 * This repository interface provides database access operations for the
 * Reading entity.
 *
 * It extends JpaRepository, which provides built-in CRUD functionality such
 * as saving, updating, deleting, and retrieving weather readings.
 *
 * Custom query methods include:
 *
 * - findBySensorIdAndMetricAndTimestampBetween():
 *   Retrieves readings for a specific sensor and metric within a given
 *   date/time range.
 *
 * - findFirstBySensorIdAndMetricOrderByTimestampDesc():
 *   Retrieves the most recent reading for a specific sensor and metric.
 *
 * - findDistinctSensorIds():
 *   Returns a list of unique sensor IDs currently stored in the database.
 *
 * These methods allow the service layer to efficiently retrieve and
 * process weather sensor data without directly writing database access
 * logic.
 */



package com.weatherapi.repository;

import com.weatherapi.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReadingRepository extends JpaRepository<Reading, Long> {


    List<Reading> findBySensorIdAndMetricAndTimestampBetween(
            String sensorId, String metric, Instant from, Instant to);

    Optional<Reading> findFirstBySensorIdAndMetricOrderByTimestampDesc(String sensorId, String metric);

    @Query("select distinct r.sensorId from Reading r")
    List<String> findDistinctSensorIds();
}
