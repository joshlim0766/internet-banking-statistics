package com.kakaopay.homework.internetbanking.controller;

import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsResponse;
import com.kakaopay.homework.internetbanking.service.InternetBankingStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/statistics")
public class InternetBankingStatisticsController {

    @Autowired
    private InternetBankingStatisticsService internetBankingStatisticsService;

    @PostMapping(
            value = "/load"
    )
    public ResponseEntity<?> loadData () {
        internetBankingStatisticsService.loadData();
        return ResponseEntity.ok().build();
    }

    @GetMapping(
            value = "/devices",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public DeviceStatisticsResponse getDevices () {
        return internetBankingStatisticsService.getDevices();
    }

    @GetMapping(
            value = "/device/yearly",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public DeviceStatisticsResponse getDeviceStatistics (@RequestParam(value="year", required = false) Short year) {
        DeviceStatisticsResponse response = null;
        if (year != null) {
            response = internetBankingStatisticsService.getDeviceStatisticsByYear(year);
        }
        else {
            response = internetBankingStatisticsService.getYearlyDeviceStatistics();
        }

        return response;
    }

    @GetMapping(
            value = "/device/maxrateyear",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public DeviceStatisticsResponse getMaxRateYear (@RequestParam(value="device_id") String deviceId) {
        return internetBankingStatisticsService.getMaxRateYear(deviceId);
    }
}
