package com.kakaopay.homework.internetbanking.controller;

import com.kakaopay.homework.internetbanking.controller.dto.RefreshTokenResponse;
import com.kakaopay.homework.internetbanking.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/token")
public class TokenController {

    @Autowired
    private JwtTokenService tokenService;

    @PostMapping(
            value = "/refresh",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public RefreshTokenResponse refreshToken (@RequestHeader(value = "Authorization") String authorization) {
        return tokenService.refreshAccessToken(authorization);
    }
}
