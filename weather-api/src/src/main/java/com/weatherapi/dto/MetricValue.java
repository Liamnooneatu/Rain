/*
 * This Data Transfer Object (DTO) represents the result of a calculated
 * weather metric returned by the API.
 *
 * stores:
 * - type: The weather metric being reported (e.g., temperature, humidity, or wind speed).
 * - measure: The unit of measurement associated with the metric  (e.g., °C, %, or km/h).
 * - value: The calculated statistic  (e.g., average, minimum, or maximum).
 *
 *  all fields are assigned through the constructor.
 */


package com.weatherapi.dto;

public class MetricValue {

    private final String type;
    private final String measure;
    private final double value;

    public MetricValue(String type, String measure, double value) {
        this.type = type;
        this.measure = measure;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getMeasure() {
        return measure;
    }

    public double getValue() {
        return value;
    }
}
