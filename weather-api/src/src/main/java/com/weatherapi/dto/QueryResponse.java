/*
 * represents the response returned from a weather data query request.
 *
 * It contains a list of SensorResult objects, where each object represents
 * the calculated metric results for an individual sensor.
 *
 * This class is used to structure the API response sent back to clients
 * after querying sensor readings, providing an organised format for returning multiple sensor results.
 */


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
