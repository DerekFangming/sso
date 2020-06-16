package com.fmning.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "key", required = false) String key) {
        System.out.println("============ " + key);
        return "login";
    }

    @GetMapping("/login1")
    public String login1() {
        return "login1111111";
    }

}
