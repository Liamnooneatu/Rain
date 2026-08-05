package com.weatherapi.dto;

import com.weatherapi.exception.InvalidQueryException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum StatisticType {
    MIN,
    MAX,
    SUM,
    AVG;

    public static StatisticType fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidQueryException("statistic must be provided (one of: min, max, sum, avg)");
        }
        try {
            return StatisticType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            List<String> valid = Arrays.stream(values())
                    .map(v -> v.name().toLowerCase())
                    .collect(Collectors.toList());
            throw new InvalidQueryException("Invalid statistic '" + raw + "'. Must be one of: " + valid);
        }
    }

    public double apply(List<Double> values) {
        return switch (this) {
            case MIN -> values.stream().mapToDouble(Double::doubleValue).min().orElseThrow();
            case MAX -> values.stream().mapToDouble(Double::doubleValue).max().orElseThrow();
            case SUM -> values.stream().mapToDouble(Double::doubleValue).sum();
            case AVG -> values.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
        };
    }
}
