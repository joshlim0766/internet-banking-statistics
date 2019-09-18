package com.kakaopay.homework.internetbanking.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceStatisticsResponse {
    @JsonProperty(value = "devices")
    private List<DeviceStatisticsDTO> deviceStatisticsList = null;

    @JsonProperty(value = "result")
    private DeviceStatisticsDTO deviceStatisticsDTO = null;
}
