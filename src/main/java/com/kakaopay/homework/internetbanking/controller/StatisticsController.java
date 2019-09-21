package com.kakaopay.homework.internetbanking.controller;

import com.kakaopay.homework.internetbanking.controller.dto.StatisticsResponse;
import com.kakaopay.homework.internetbanking.service.StatisticsService;
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
        return ResponseEntity.ok().build();
    }

    @GetMapping(
            value = "/device/yearly",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public StatisticsResponse getDeviceStatistics () {
            return statisticsService.getYearlyDeviceStatistics();
    }

    @GetMapping(
            value = "/device/yearly/{year}",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public StatisticsResponse getDeviceStatistics (@PathVariable(value="year") Short year) {
        return statisticsService.getDeviceStatisticsByYear(year);
    }

    @GetMapping(
            value = "/device/maxrateyear",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public StatisticsResponse getMaxRateYear (@RequestParam(value="device_id") String deviceId) {
        return statisticsService.getMaxRateYear(deviceId);
    }
}
