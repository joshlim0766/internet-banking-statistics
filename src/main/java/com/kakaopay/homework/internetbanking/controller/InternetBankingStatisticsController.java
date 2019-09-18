package com.kakaopay.homework.internetbanking.controller;

import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsResponse;
import com.kakaopay.homework.internetbanking.service.InternetBankingStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/statistics")
public class InternetBankingStatisticsController {

    @Autowired
    private InternetBankingStatisticsService internetBankingStatisticsService;

    @PostMapping(
            value = "/load",
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public void loadData () {
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
        return internetBankingStatisticsService.getDeviceStatistics(year);
    }

    @GetMapping(
            value = "/device/maxrateyear",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public DeviceStatisticsResponse getMaxRateYear (@RequestParam(value="device_id") String deviceId) {
        return internetBankingStatisticsService.getMaxRateYear(deviceId);
    }
}
