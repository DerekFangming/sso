package com.fmning.sso.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
public class UiController {

    @GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
//        System.out.println("============ " + app);
        String continueParamValue = UrlUtils.buildRequestUrl(request);
        model.addAttribute("params", continueParamValue);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String clientId = request.getParameter("client_id");
        return "login";
    }

    @GetMapping("/login1")
    public ModelAndView  login() {
        return new ModelAndView("redirect:/login");
    }

//    @GetMapping("/sso/login")
//    public void  loginSso(HttpServletRequest request, HttpServletResponse respone) {
//        String url = request.getRequestURL().toString();
//        respone.setHeader(HttpHeaders.LOCATION, url.replace("/sso/login", "/login"));
//        respone.setStatus(HttpStatus.FOUND.value());
//    }

    @GetMapping("/sso/login")
    public String  loginSso(HttpServletRequest request, HttpServletResponse respone) {
        String url = request.getRequestURL().toString();
        return url;
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset";
    }

}
