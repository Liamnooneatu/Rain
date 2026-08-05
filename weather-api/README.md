# Weather Sensor API

A small REST API that ingests weather sensor readings (temperature, humidity, wind speed,
etc.) and lets you query them back with aggregation (min/max/sum/avg) over a date range.

## Tech stack

- Java 21, Spring Boot 3.3 (Web, Data JPA, Validation)
- PostgreSQL (via Docker Compose) for persistence, with an H2 in-memory profile for a
  zero-setup run and for automated tests
- JUnit 5, Mockito, MockMvc

## How to run

### Option A — with Docker (Postgres, closer to "real")

```bash
docker compose up -d          # starts Postgres on localhost:5432
./mvnw spring-boot:run         # or run WeatherApiApplication from your IDE
```

### Option B — no Docker (in-memory H2, quickest to try)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

Data does not persist across restarts in this mode. H2 console (if you want to poke at the
data directly) is at `http://localhost:8080/h2-console`, JDBC URL `jdbc:h2:mem:weatherdb`.

The API listens on `http://localhost:8080`.

### Running tests

```bash
./mvnw test
```

Tests run against the H2 profile, so no Docker/Postgres is required to run the test suite.

## API

### Ingest a reading

```
POST /api/v1/sensors/{sensorId}/readings
Content-Type: application/json

{
  "metric": "temperature",
  "value": 21.5,
  "timestamp": "2026-08-03T10:00:00Z"
}
```

`timestamp` is optional — if omitted, the server stamps the current time. `metric` names are
normalized to lowercase on write and on query, so `Temperature`/`temperature` are treated the
same.

Returns `201 Created` with the saved reading.

### Query sensor data

```
GET /api/v1/readings/query?sensorIds=sensor-1,sensor-2&metrics=temperature,humidity&statistic=avg&from=2026-07-27T00:00:00Z&to=2026-08-03T00:00:00Z
```

| Param | Required | Notes |
|---|---|---|
| `sensorIds` | No | Comma-separated. Omit to include **all** sensors that have reported data. |
| `metrics` | Yes | Comma-separated, e.g. `temperature,humidity`. |
| `statistic` | Yes | One of `min`, `max`, `sum`, `avg`. |
| `from`, `to` | No | ISO-8601 instants. Must be supplied **together**. If omitted, the latest reading per sensor/metric is returned. If supplied, the range must be between 1 day and 1 month (31 days). |

Example — "average temperature and humidity for sensor 1 in the last week":

```
GET /api/v1/readings/query?sensorIds=sensor-1&metrics=temperature,humidity&statistic=avg&from=2026-07-27T00:00:00Z&to=2026-08-03T00:00:00Z
```

Response — one row per (sensor, metric) combination:

```json
[
  {
    "sensorId": "sensor-1",
    "metric": "temperature",
    "statistic": "avg",
    "value": 19.4,
    "dataPointCount": 42,
    "rangeFrom": "2026-07-27T00:00:00Z",
    "rangeTo": "2026-08-03T00:00:00Z"
  },
  {
    "sensorId": "sensor-1",
    "metric": "humidity",
    "statistic": "avg",
    "value": 61.2,
    "dataPointCount": 42,
    "rangeFrom": "2026-07-27T00:00:00Z",
    "rangeTo": "2026-08-03T00:00:00Z"
  }
]
```

If a sensor/metric combination has no data in the requested range, it's simply omitted from
the response rather than causing the whole query to fail — this keeps multi-sensor/multi-metric
queries useful even when coverage is patchy.

## Design decisions & trade-offs

- **Aggregation is done in the application layer (Java streams), not in SQL.** For a fixed set
  of statistics this could be pushed down to the database with `GROUP BY` + `MIN/MAX/SUM/AVG`,
  which would scale better for very large datasets. I chose to do it in-memory for this PoC
  because the statistic is dynamic (chosen per-request) and the data volumes involved don't
  warrant the extra query complexity yet. This is the first thing I'd change for a
  production-scale version.
- **One result row per (sensor, metric) pair**, rather than a single blended number across all
  requested sensors. This matches how the example query reads ("temperature and humidity for
  sensor 1") and is more useful when multiple sensors are requested, since collapsing sensors
  together would hide per-sensor differences.
- **Postgres for the "real" datastore.** The query pattern (filter by sensor + metric + time
  range, aggregate a numeric value) is a natural fit for a relational table with a composite
  index — it doesn't need the flexible schema of a document store or the access-pattern-first
  design that DynamoDB would demand for this same query shape.
- **H2 profile** exists purely so a reviewer can run the app / tests without installing Docker
  or Postgres locally.
- **Metric and sensor IDs are treated as free-form strings**, not a fixed enum/registry. A
  sensor "registers" implicitly the first time it reports a reading. This was a deliberate
  scope cut — a real system would likely have a separate sensor-registration/metadata concept.

## What's incomplete / would add with more time

- No authentication/authorization on the endpoints.
- No pagination on the query endpoint (fine for the volumes in a PoC, not for production).
- No rate limiting or duplicate-reading detection on ingestion.
- Aggregation is O(n) in application memory per sensor/metric — would push to SQL-side
  aggregation for larger datasets.
- No OpenAPI/Swagger spec generated, though the endpoints are documented above and in Javadoc
  on the controller.
- Testcontainers-based integration tests against a real Postgres (currently tests run against
  H2, which is close enough for this scope but not 100% identical to Postgres behavior).

## AI assistance disclosure

Parts of this solution were built with the help of an AI coding assistant (Claude). A summary
of the prompts used is in `AI_PROMPTS.md`.
