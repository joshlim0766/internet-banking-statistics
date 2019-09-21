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

    public StatisticsDTO(String deviceName, short year, double rate) {
        this.deviceName = deviceName;
        this.year = year;
        this.rate = rate;
    }

    @JsonProperty(value = "year")
    private Short year;

    @JsonProperty(value = "device_name")
    private String deviceName;

    @JsonProperty(value = "rate")
    private Double rate;
}
