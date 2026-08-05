package com.weatherapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class ReadingControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void ingestThenQuery_returnsExpectedAverage_nestedUnderSensor() throws Exception {
        Instant now = Instant.now();

        ingest("sensor-nested-1", "temperature", 10.0, now.minus(1, ChronoUnit.HOURS), "Galway City", "53.27,-9.05");
        ingest("sensor-nested-1", "temperature", 20.0, now.minus(30, ChronoUnit.MINUTES), null, null);
        ingest("sensor-nested-1", "temperature", 30.0, now, null, null);

        Instant from = now.minus(2, ChronoUnit.DAYS);
        Instant to = now.plus(1, ChronoUnit.HOURS);

        mockMvc.perform(get("/api/v1/readings/query")
                        .param("sensorIds", "sensor-nested-1")
                        .param("metrics", "temperature")
                        .param("statistic", "avg")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensors[0].id").value("sensor-nested-1"))
                .andExpect(jsonPath("$.sensors[0].location").value("Galway City"))
                .andExpect(jsonPath("$.sensors[0].statistics").value("AVG"))
                .andExpect(jsonPath("$.sensors[0].metrics[0].type").value("TEMPERATURE"))
                .andExpect(jsonPath("$.sensors[0].metrics[0].measure").value("C"))
                .andExpect(jsonPath("$.sensors[0].metrics[0].value").value(20.0))
                .andExpect(jsonPath("$.sensors[0].range.from_date").exists())
                .andExpect(jsonPath("$.sensors[0].range.to_date").exists());
    }

    @Test
    void ingest_rejectsMissingMetric() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("value", 21.5));

        mockMvc.perform(post("/api/v1/sensors/sensor-x/readings")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ingest_rejectsUnknownMetric() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("metric", "pressure", "value", 1013.0));

        mockMvc.perform(post("/api/v1/sensors/sensor-x/readings")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(org.hamcrest.Matchers.containsString("Invalid metric")));
    }

    @Test
    void query_rejectsInvalidStatistic() throws Exception {
        mockMvc.perform(get("/api/v1/readings/query")
                        .param("metrics", "temperature")
                        .param("statistic", "median"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(org.hamcrest.Matchers.containsString("Invalid statistic")));
    }

    @Test
    void query_rejectsDateRangeTooShort() throws Exception {
        Instant now = Instant.now();
        mockMvc.perform(get("/api/v1/readings/query")
                        .param("metrics", "temperature")
                        .param("statistic", "avg")
                        .param("from", now.minus(1, ChronoUnit.HOURS).toString())
                        .param("to", now.toString()))
                .andExpect(status().isBadRequest());
    }

    private void ingest(String sensorId, String metric, double value, Instant timestamp,
                         String location, String gps) throws Exception {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("metric", metric);
        body.put("value", value);
        body.put("timestamp", timestamp.toString());
        if (location != null) body.put("location", location);
        if (gps != null) body.put("gps", gps);

        mockMvc.perform(post("/api/v1/sensors/" + sensorId + "/readings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }
}
