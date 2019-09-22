package com.kakaopay.homework.controller;

import com.kakaopay.homework.controller.dto.RefreshTokenResponse;
import com.kakaopay.homework.service.JwtTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Token", tags = {"Token"})
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/token")
public class TokenController {

    @Autowired
    private JwtTokenService tokenService;

    @ApiOperation(value = "토큰을 재발급")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "authorization header", required = true,
                    dataType = "string", paramType = "header", defaultValue = "Bearer "
            )
    })
    @PostMapping(
            value = "/refresh",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public RefreshTokenResponse refreshToken (@RequestHeader(value = "Authorization") String authorization) {
        return tokenService.refreshAccessToken(authorization);
    }
}
