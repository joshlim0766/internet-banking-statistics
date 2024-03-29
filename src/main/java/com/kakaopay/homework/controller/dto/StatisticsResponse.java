package com.kakaopay.homework.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsResponse {
    @JsonProperty(value = "devices")
    private List<StatisticsDTO> deviceStatisticsList = null;

    @JsonProperty(value = "result")
    private StatisticsDTO statisticsDTO = null;
}
