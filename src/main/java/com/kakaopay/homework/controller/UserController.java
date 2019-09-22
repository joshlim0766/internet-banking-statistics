package com.kakaopay.homework.controller;

import com.kakaopay.homework.controller.dto.LoginResponse;
import com.kakaopay.homework.controller.dto.SingupResponse;
import com.kakaopay.homework.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            value = "/signin",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    public LoginResponse login (@RequestBody MultiValueMap<String, String> loginInformation) {
        return userService.login(loginInformation);
    }
}
