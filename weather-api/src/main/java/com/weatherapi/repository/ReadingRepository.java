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
