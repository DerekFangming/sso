package com.fmning.sso.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class UiController {

    private final ServletContext servletContext;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("contextPath", servletContext.getContextPath());
        return "dashboard";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        model.addAttribute("contextPath", servletContext.getContextPath());
        model.addAttribute("loginUrl", UrlUtils.buildRequestUrl(request));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String clientId = request.getParameter("client_id");
        return "login";
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("contextPath", servletContext.getContextPath());
        return "register";
    }

    @GetMapping("/reset-password")
    public String resetPassword(Model model) {
        model.addAttribute("contextPath", servletContext.getContextPath());
        return "reset";
    }

}
