/*
 * represents the result of a weather sensor query for an individual sensor.
 *
 * stores:
 * - id: The unique identifier of the sensor.
 * - location: The physical location associated with the sensor.
 * - gps: The GPS coordinates of the sensor.
 * - range: The date/time range that the query was performed over.
 * - statistics: The statistical operation applied to the readings (e.g., average, minimum, or maximum).
 * - metrics: A list of calculated metric values returned for the sensor.
 *
 * used to structure the API response by grouping sensor
 * information and calculated weather statistics into a single object.
 *
 * all fields are final and values are assigned through the constructor.
 */


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
