package com.kakaopay.homework.controller;

import com.kakaopay.homework.controller.dto.StatisticsResponse;
import com.kakaopay.homework.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping(
            value = "/load"
    )
    public ResponseEntity<?> loadData () {
        statisticsService.loadData();
        return ResponseEntity.ok("{\"result\" : \"success\"}");
    }

    @GetMapping(
            value = "/devices/first_rank",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public StatisticsResponse getFirstRankDevices () {
            return statisticsService.getFirstRankDevices();
    }

    @GetMapping(
            value = "/{year}/devices/first_rank",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public StatisticsResponse getFirstRankDevice (@PathVariable(value="year") Short year) {
        return statisticsService.getFirstRankDevice(year);
    }

    @GetMapping(
            value = "/devices/{device_id}/first_rank_year",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public StatisticsResponse getFirstRankYear(@PathVariable(value="device_id") String deviceId) {
        return statisticsService.getFirstRankYear(deviceId);
    }
}
