package com.fmning.sso.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class UtilController {

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/encode-password/{password}")
    public String encodePassword(@PathVariable("password") String password) {
         return passwordEncoder.encode(password);
    }
}
