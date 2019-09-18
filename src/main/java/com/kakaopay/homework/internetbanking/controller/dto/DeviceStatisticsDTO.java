package com.kakaopay.homework.internetbanking.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class DeviceStatisticsDTO {

    public DeviceStatisticsDTO(int uid, String deviceId, String deviceName, short year, double rate) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.year = year;
        this.rate = rate;
    }

    @JsonProperty(value = "year")
    private Short year;

    @JsonProperty(value = "device_id")
    private String deviceId;

    @JsonProperty(value = "device_name")
    private String deviceName;

    @JsonProperty(value = "rate")
    private Double rate;
}
