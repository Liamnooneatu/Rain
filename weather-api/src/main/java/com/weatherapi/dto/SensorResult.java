package com.weatherapi.dto;

import java.util.List;

public class SensorResult {

    private final String id;
    private final String location;
    private final String gps;
    private final DateRange range;
    private final String statistics;
    private final List<MetricValue> metrics;

    public SensorResult(String id, String location, String gps, DateRange range,
                         String statistics, List<MetricValue> metrics) {
        this.id = id;
        this.location = location;
        this.gps = gps;
        this.range = range;
        this.statistics = statistics;
        this.metrics = metrics;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getGps() {
        return gps;
    }

    public DateRange getRange() {
        return range;
    }

    public String getStatistics() {
        return statistics;
    }

    public List<MetricValue> getMetrics() {
        return metrics;
    }
}
