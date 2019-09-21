package com.kakaopay.homework.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class StatisticsDTO {

    public StatisticsDTO(String deviceName, short year, Double rate, Double overallRate) {
        this.deviceName = deviceName;
        this.year = year;
        this.rate = rate;
        this.overallRate = overallRate;
    }

    @JsonProperty(value = "year")
    private Short year;

    @JsonProperty(value = "device_name")
    private String deviceName;

    @JsonProperty(value = "rate")
    private Double rate;

    @JsonProperty(value = "overall_rate")
    private Double overallRate;
}
