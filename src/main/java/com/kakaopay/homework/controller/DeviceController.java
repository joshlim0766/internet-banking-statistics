package com.kakaopay.homework.controller;

import com.kakaopay.homework.controller.dto.DeviceResponse;
import com.kakaopay.homework.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Device", tags = {"Device"})
@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @ApiOperation(value = "인터넷뱅킹 서비스 접속 기기 목록을 출력")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "authorization header", required = true,
                    dataType = "string", paramType = "header", defaultValue = "Bearer "
            )
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public DeviceResponse getDevices () {
        return deviceService.getDevices();
    }
}
