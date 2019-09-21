package com.kakaopay.homework.controller;

import com.kakaopay.homework.controller.dto.DeviceResponse;
import com.kakaopay.homework.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public DeviceResponse getDevices () {
        return deviceService.getDevices();
    }
}
