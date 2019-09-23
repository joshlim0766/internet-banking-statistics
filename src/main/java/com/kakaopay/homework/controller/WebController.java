package com.kakaopay.homework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController {

    @GetMapping(value = "/")
    public ModelAndView index () {
        ModelAndView mv = new ModelAndView();

        mv.setViewName("index");
        mv.addObject("clientId", "client");

        return mv;
    }

    @GetMapping(value = "/favicon.ico")
    @ResponseBody
    public void favIcon () {
    }
}
