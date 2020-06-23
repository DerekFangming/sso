package com.fmning.sso.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
//        System.out.println("============ " + app);
        String continueParamValue = UrlUtils.buildRequestUrl(request);
        model.addAttribute("params", continueParamValue);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String clientId = request.getParameter("client_id");
        return "login";
    }

    @GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }

}
