package com.weatherapi.service;

import com.weatherapi.dto.*;
import com.weatherapi.exception.InvalidQueryException;
import com.weatherapi.model.Reading;
import com.weatherapi.model.SensorMetadata;
import com.weatherapi.repository.ReadingRepository;
import com.weatherapi.repository.SensorMetadataRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ReadingService {

    private static final long MIN_RANGE_DAYS = 1;
    private static final long MAX_RANGE_DAYS = 31;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    private final ReadingRepository readingRepository;
    private final SensorMetadataRepository sensorMetadataRepository;

    public ReadingService(ReadingRepository readingRepository, SensorMetadataRepository sensorMetadataRepository) {
        this.readingRepository = readingRepository;
        this.sensorMetadataRepository = sensorMetadataRepository;
    }

    public Reading ingest(String sensorId, ReadingRequest request) {
        if (sensorId == null || sensorId.isBlank()) {
            throw new InvalidQueryException("sensorId must not be blank");
        }
        Metric metric = Metric.fromString(request.getMetric());
        upsertSensorMetadata(sensorId, request.getLocation(), request.getGps());

        Instant timestamp = request.getTimestamp() != null ? request.getTimestamp() : Instant.now();
        Reading reading = new Reading(sensorId, metric.name(), request.getValue(), timestamp);
        return readingRepository.save(reading);
    }

    private void upsertSensorMetadata(String sensorId, String location, String gps) {
        SensorMetadata existing = sensorMetadataRepository.findById(sensorId).orElse(null);
        if (existing == null) {
            sensorMetadataRepository.save(new SensorMetadata(
                    sensorId,
                    location != null ? location : "Unknown",
                    gps));
        } else {
            // Only overwrite if the caller actually supplied a new value this time.
            if (location != null) existing.setLocation(location);
            if (gps != null) existing.setGps(gps);
            sensorMetadataRepository.save(existing);
        }
    }

    public QueryResponse query(List<String> sensorIds, List<String> metricsRaw,
                                String statisticRaw, Instant from, Instant to) {

        StatisticType statistic = StatisticType.fromString(statisticRaw);
        List<Metric> metrics = validateAndNormalizeMetrics(metricsRaw);
        validateDateRange(from, to);

        List<String> resolvedSensorIds = (sensorIds == null || sensorIds.isEmpty())
                ? readingRepository.findDistinctSensorIds()
                : sensorIds;

        if (resolvedSensorIds.isEmpty()) {
            throw new InvalidQueryException("No sensors found to query (no data has been ingested yet)");
        }

        List<SensorResult> sensorResults = new ArrayList<>();
        for (String sensorId : resolvedSensorIds) {
            SensorResult result = buildSensorResult(sensorId, metrics, statistic, from, to);
            if (result != null) {
                sensorResults.add(result);
            }
        }
        return new QueryResponse(sensorResults);
    }

    private SensorResult buildSensorResult(String sensorId, List<Metric> metrics, StatisticType statistic,
                                            Instant from, Instant to) {
        List<MetricValue> metricValues = new ArrayList<>();
        Instant earliest = null;
        Instant latest = null;

        for (Metric metric : metrics) {
            MetricComputation computation = computeMetric(sensorId, metric, statistic, from, to);
            if (computation == null) {
                continue; // no data for this sensor/metric combination - simply omit it
            }
            metricValues.add(new MetricValue(metric.name(), metric.getUnit(), computation.value()));
            if (earliest == null || computation.earliestTimestamp().isBefore(earliest)) {
                earliest = computation.earliestTimestamp();
            }
            if (latest == null || computation.latestTimestamp().isAfter(latest)) {
                latest = computation.latestTimestamp();
            }
        }

        if (metricValues.isEmpty()) {
            return null; // this sensor had no data for any requested metric - omit from response
        }

        SensorMetadata metadata = sensorMetadataRepository.findById(sensorId).orElse(null);
        String location = metadata != null ? metadata.getLocation() : "Unknown";
        String gps = metadata != null ? metadata.getGps() : null;

        DateRange range = new DateRange(DATE_FORMAT.format(earliest), DATE_FORMAT.format(latest));

        return new SensorResult(sensorId, location, gps, range, statistic.name(), metricValues);
    }

    private MetricComputation computeMetric(String sensorId, Metric metric, StatisticType statistic,
                                             Instant from, Instant to) {
        // No date range supplied -> "the latest data should be queried"
        if (from == null && to == null) {
            return readingRepository.findFirstBySensorIdAndMetricOrderByTimestampDesc(sensorId, metric.name())
                    .map(latest -> new MetricComputation(latest.getValue(), latest.getTimestamp(), latest.getTimestamp()))
                    .orElse(null);
        }

        List<Reading> readings = readingRepository.findBySensorIdAndMetricAndTimestampBetween(
                sensorId, metric.name(), from, to);
        if (readings.isEmpty()) {
            return null;
        }

        List<Double> values = readings.stream().map(Reading::getValue).toList();
        double statValue = statistic.apply(values);

        Instant earliest = readings.stream().map(Reading::getTimestamp).min(Instant::compareTo).orElse(from);
        Instant latestTs = readings.stream().map(Reading::getTimestamp).max(Instant::compareTo).orElse(to);
        return new MetricComputation(statValue, earliest, latestTs);
    }

    private List<Metric> validateAndNormalizeMetrics(List<String> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            throw new InvalidQueryException("At least one metric must be specified");
        }
        List<Metric> parsed = new ArrayList<>();
        for (String raw : metrics) {
            Metric metric = Metric.fromString(raw);
            if (!parsed.contains(metric)) {
                parsed.add(metric);
            }
        }
        return parsed;
    }

    private void validateDateRange(Instant from, Instant to) {
        if (from == null && to == null) {
            return; // valid: means "latest"
        }
        if (from == null || to == null) {
            throw new InvalidQueryException("Both 'from' and 'to' must be provided together, or neither (for latest data)");
        }
        if (from.isAfter(to)) {
            throw new InvalidQueryException("'from' must not be after 'to'");
        }
        long days = ChronoUnit.DAYS.between(from, to);
        if (days < MIN_RANGE_DAYS) {
            throw new InvalidQueryException("Date range must be at least 1 day");
        }
        if (days > MAX_RANGE_DAYS) {
            throw new InvalidQueryException("Date range must not exceed 1 month (31 days)");
        }
    }

    /** Internal holding type: the computed statistic value plus the span of readings it covers. */
    private record MetricComputation(double value, Instant earliestTimestamp, Instant latestTimestamp) {
    }
}
