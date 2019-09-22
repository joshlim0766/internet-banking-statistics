package com.kakaopay.homework.controller;

import com.kakaopay.homework.controller.dto.ForecastRequest;
import com.kakaopay.homework.controller.dto.StatisticsResponse;
import com.kakaopay.homework.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "Statistics", tags = {"Statistics"})
@RestController
@RequestMapping(value = "/api/v1/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation(value = "데이터 파일에서 잀어서 각 레코드를 데이터베이스에 저장")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "authorization header", required = true,
                    dataType = "string", paramType = "header", defaultValue = "Bearer "
            )
    })
    @PostMapping(
            value = "/load"
    )
    public ResponseEntity<?> loadData () {
        statisticsService.loadData();
        return ResponseEntity.ok("{\"result\" : \"success\"}");
    }

    @ApiOperation(value = "각 년도별로 인터넷뱅킹을 가장 많이 이용하는 접속기기를 출력")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "authorization header", required = true,
                    dataType = "string", paramType = "header", defaultValue = "Bearer "
            )
    })
    @GetMapping(
            value = "/devices/first_rank",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public StatisticsResponse getFirstRankDevices () {
            return statisticsService.getFirstRankDevices();
    }

    @ApiOperation(value = "특정 년도를 입력받아 그 해에 인터넷뱅킹에 가장 많이 접속하는 기기 이름을 출력")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "authorization header", required = true,
                    dataType = "string", paramType = "header", defaultValue = "Bearer "
            )
    })
    @GetMapping(
            value = "/{year}/devices/first_rank",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public StatisticsResponse getFirstRankDevice (@PathVariable(value="year") Short year) {
        return statisticsService.getFirstRankDevice(year);
    }

    @ApiOperation(value = "디바이스 아이디를 입력받아 인터넷뱅킹에 접속 비율이 가장 많은 해를 출력")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "authorization header", required = true,
                    dataType = "string", paramType = "header", defaultValue = "Bearer "
            )
    })
    @GetMapping(
            value = "/devices/{device_id}/first_rank_year",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public StatisticsResponse getFirstRankYear(@PathVariable(value="device_id") String deviceId) {
        return statisticsService.getFirstRankYear(deviceId);
    }

    @ApiOperation(value = "인터넷뱅킹 접속 기기 ID 를 입력받아 2019 년도 인터넷뱅킹 접속 비율을 예측")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "authorization header", required = true,
                    dataType = "string", paramType = "header", defaultValue = "Bearer "
            )
    })
    @PostMapping(
            value = "/devices/forecast",
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public StatisticsResponse forecast (@RequestBody ForecastRequest forecastRequest) {
        return statisticsService.forecast(forecastRequest);
    }
}
