package com.weatherapi.service;

import com.weatherapi.dto.QueryResponse;
import com.weatherapi.dto.ReadingRequest;
import com.weatherapi.dto.SensorResult;
import com.weatherapi.exception.InvalidQueryException;
import com.weatherapi.model.Reading;
import com.weatherapi.model.SensorMetadata;
import com.weatherapi.repository.ReadingRepository;
import com.weatherapi.repository.SensorMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadingServiceTest {

    @Mock
    ReadingRepository readingRepository;

    @Mock
    SensorMetadataRepository sensorMetadataRepository;

    ReadingService service;

    @BeforeEach
    void setUp() {
        service = new ReadingService(readingRepository, sensorMetadataRepository);
    }

    @Test
    void ingest_normalizesMetricToEnumName_andSavesReading() {
        ReadingRequest request = new ReadingRequest();
        request.setMetric("temperature"); // lower case input, should be normalized
        request.setValue(21.5);

        when(sensorMetadataRepository.findById("sensor-1")).thenReturn(Optional.empty());
        when(readingRepository.save(any(Reading.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reading saved = service.ingest("sensor-1", request);

        assertThat(saved.getSensorId()).isEqualTo("sensor-1");
        assertThat(saved.getMetric()).isEqualTo("TEMPERATURE");
        assertThat(saved.getValue()).isEqualTo(21.5);
    }

    @Test
    void ingest_rejectsUnknownMetric() {
        ReadingRequest request = new ReadingRequest();
        request.setMetric("pressure"); // not in the Metric enum
        request.setValue(1013.0);

        assertThatThrownBy(() -> service.ingest("sensor-1", request))
                .isInstanceOf(InvalidQueryException.class)
                .hasMessageContaining("Invalid metric");
    }

    @Test
    void query_returnsAverage_nestedUnderSensor_withLocationAndGps() {
        Instant to = Instant.now();
        Instant from = to.minus(2, ChronoUnit.DAYS);

        List<Reading> readings = List.of(
                new Reading("sensor-1", "TEMPERATURE", 10.0, from.plusSeconds(10)),
                new Reading("sensor-1", "TEMPERATURE", 20.0, from.plusSeconds(20)),
                new Reading("sensor-1", "TEMPERATURE", 30.0, from.plusSeconds(30))
        );
        when(readingRepository.findBySensorIdAndMetricAndTimestampBetween("sensor-1", "TEMPERATURE", from, to))
                .thenReturn(readings);
        when(sensorMetadataRepository.findById("sensor-1"))
                .thenReturn(Optional.of(new SensorMetadata("sensor-1", "Galway City", "53.27,-9.05")));

        QueryResponse response = service.query(List.of("sensor-1"), List.of("temperature"), "avg", from, to);

        assertThat(response.getSensors()).hasSize(1);
        SensorResult sensor = response.getSensors().get(0);
        assertThat(sensor.getId()).isEqualTo("sensor-1");
        assertThat(sensor.getLocation()).isEqualTo("Galway City");
        assertThat(sensor.getGps()).isEqualTo("53.27,-9.05");
        assertThat(sensor.getStatistics()).isEqualTo("AVG");
        assertThat(sensor.getMetrics()).hasSize(1);
        assertThat(sensor.getMetrics().get(0).getType()).isEqualTo("TEMPERATURE");
        assertThat(sensor.getMetrics().get(0).getMeasure()).isEqualTo("C");
        assertThat(sensor.getMetrics().get(0).getValue()).isEqualTo(20.0);
    }

    @Test
    void query_defaultsLocationToUnknown_whenNoMetadataStored() {
        Instant now = Instant.now();
        Reading latest = new Reading("sensor-2", "HUMIDITY", 55.0, now);
        when(readingRepository.findFirstBySensorIdAndMetricOrderByTimestampDesc("sensor-2", "HUMIDITY"))
                .thenReturn(Optional.of(latest));
        when(sensorMetadataRepository.findById("sensor-2")).thenReturn(Optional.empty());

        QueryResponse response = service.query(List.of("sensor-2"), List.of("humidity"), "max", null, null);

        assertThat(response.getSensors()).hasSize(1);
        assertThat(response.getSensors().get(0).getLocation()).isEqualTo("Unknown");
        assertThat(response.getSensors().get(0).getGps()).isNull();
    }

    @Test
    void query_rejectsDateRangeShorterThanOneDay() {
        Instant to = Instant.now();
        Instant from = to.minus(2, ChronoUnit.HOURS);

        assertThatThrownBy(() -> service.query(List.of("sensor-1"), List.of("temperature"), "avg", from, to))
                .isInstanceOf(InvalidQueryException.class)
                .hasMessageContaining("at least 1 day");
    }

    @Test
    void query_rejectsDateRangeLongerThanOneMonth() {
        Instant to = Instant.now();
        Instant from = to.minus(60, ChronoUnit.DAYS);

        assertThatThrownBy(() -> service.query(List.of("sensor-1"), List.of("temperature"), "avg", from, to))
                .isInstanceOf(InvalidQueryException.class)
                .hasMessageContaining("not exceed 1 month");
    }

    @Test
    void query_rejectsUnknownStatistic() {
        assertThatThrownBy(() -> service.query(List.of("sensor-1"), List.of("temperature"), "median", null, null))
                .isInstanceOf(InvalidQueryException.class)
                .hasMessageContaining("Invalid statistic");
    }

    @Test
    void query_rejectsUnknownMetric() {
        assertThatThrownBy(() -> service.query(List.of("sensor-1"), List.of("pressure"), "avg", null, null))
                .isInstanceOf(InvalidQueryException.class)
                .hasMessageContaining("Invalid metric");
    }

    @Test
    void query_rejectsEmptyMetricList() {
        assertThatThrownBy(() -> service.query(List.of("sensor-1"), List.of(), "avg", null, null))
                .isInstanceOf(InvalidQueryException.class)
                .hasMessageContaining("At least one metric");
    }

    @Test
    void query_rejectsOnlyOneOfFromTo() {
        Instant from = Instant.now().minus(2, ChronoUnit.DAYS);

        assertThatThrownBy(() -> service.query(List.of("sensor-1"), List.of("temperature"), "avg", from, null))
                .isInstanceOf(InvalidQueryException.class)
                .hasMessageContaining("must be provided together");
    }
}
