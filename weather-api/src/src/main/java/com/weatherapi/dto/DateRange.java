/*
 * This Data Transfer Object (DTO) represents the date range used when
 * querying weather sensor data.
 *
 * It stores:
 * - fromDate: The start date/time of the query range.
 * - toDate: The end date/time of the query range.
 *
 * The @JsonProperty annotations map the Java field names to the JSON
 * property names ("from_date" and "to_date") when sending responses.
 */

package com.weatherapi.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DateRange {

    @JsonProperty("from_date")
    private final String fromDate;

    @JsonProperty("to_date")
    private final String toDate;

    public DateRange(String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getFromDate() {
        return fromDate;
    }
    public String getToDate() {
        return toDate;
    }
}
