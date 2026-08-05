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
