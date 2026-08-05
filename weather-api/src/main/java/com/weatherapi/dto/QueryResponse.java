package com.weatherapi.dto;

import java.util.List;

public class QueryResponse {

    private final List<SensorResult> sensors;

    public QueryResponse(List<SensorResult> sensors) {
        this.sensors = sensors;
    }

    public List<SensorResult> getSensors() {
        return sensors;
    }
}
