package com.weatherapi.dto;

import com.weatherapi.exception.InvalidQueryException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public enum Metric {
    TEMPERATURE("C"),
    HUMIDITY("%"),
    WIND_SPEED("km/h");

    private final String unit;
    Metric(String unit) {
        this.unit = unit;
    }
    public String getUnit() {
        return unit;
    }

    public static Metric fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidQueryException("metric must not be blank");
        }
        try {
            return Metric.valueOf(raw.trim().toUpperCase().replace('-', '_').replace(' ', '_'));
        } catch (IllegalArgumentException e) {
            List<String> valid = Arrays.stream(values()).map(Enum::name).collect(Collectors.toList());
            throw new InvalidQueryException("Invalid metric '" + raw + "'. Must be one of: " + valid);
        }
    }
}
