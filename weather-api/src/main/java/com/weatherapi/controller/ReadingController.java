package com.weatherapi.controller;

import com.weatherapi.dto.QueryResponse;
import com.weatherapi.dto.ReadingRequest;
import com.weatherapi.model.Reading;
import com.weatherapi.service.ReadingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ReadingController {

    private final ReadingService service;
    public ReadingController(ReadingService service) {
        this.service = service;
    }


    @PostMapping("/sensors/{sensorId}/readings")
    public ResponseEntity<Reading> ingest(@PathVariable String sensorId,
                                           @Valid @RequestBody ReadingRequest request) {
        Reading saved = service.ingest(sensorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/readings/query")
    public ResponseEntity<QueryResponse> query(
            @RequestParam(required = false) List<String> sensorIds,
            @RequestParam List<String> metrics,
            @RequestParam String statistic,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        QueryResponse response = service.query(sensorIds, metrics, statistic, from, to);
        return ResponseEntity.ok(response);
    }
}
