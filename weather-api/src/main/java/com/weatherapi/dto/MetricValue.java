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
