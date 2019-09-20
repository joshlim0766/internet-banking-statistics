package com.kakaopay.homework.internetbanking.controller;

import com.kakaopay.homework.internetbanking.controller.dto.LoginResponse;
import com.kakaopay.homework.internetbanking.controller.dto.RefreshTokenResponse;
import com.kakaopay.homework.internetbanking.controller.dto.SingupResponse;
import com.kakaopay.homework.internetbanking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(
            value = "/signup",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public SingupResponse singup (@RequestBody MultiValueMap<String, String> signupInformation) {
        return userService.signup(signupInformation);
    }

    @PostMapping(
            value = "/login",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public LoginResponse login (@RequestBody MultiValueMap<String, String> loginInformation) {
        return userService.login(loginInformation);
    }
}
