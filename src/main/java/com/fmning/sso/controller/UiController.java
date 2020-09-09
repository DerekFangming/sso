package com.fmning.sso.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UiController {

    @GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
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

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset";
    }

}
