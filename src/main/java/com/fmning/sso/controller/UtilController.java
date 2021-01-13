package com.fmning.sso.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/ping1")
    public String ping1() {
        SecurityContext sc = SecurityContextHolder.getContext();
        return "ok";
    }

    @GetMapping("/test")
    public ResponseEntity test() throws Exception {
//        Thread.sleep(5000);
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/ping2")
    @PreAuthorize("hasRole('SSO')")
    public String ping2() {
        return "ok";
    }

    @GetMapping("/ping3")
    @PreAuthorize("hasRole('USER')")
    public String ping3() {
        return "ok";
    }


}
