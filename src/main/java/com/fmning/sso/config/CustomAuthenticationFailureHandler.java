package com.fmning.sso.config;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    ServletContext servletContext;

    public CustomAuthenticationFailureHandler(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String continueParamValue = UrlUtils.buildRequestUrl(request);

        String prompt = "error";
        if (exception instanceof DisabledException) {
            prompt = "disabled";
        }

        if (request.getParameter("prompt") == null) {
            if (request.getQueryString() == null) {
                continueParamValue += "?prompt=" + prompt;
            } else {
                continueParamValue += "&prompt=" + prompt;
            }
        } else {
            continueParamValue = continueParamValue.replace("prompt=" + request.getParameter("prompt"),
                    "prompt=" + prompt);
        }
        response.sendRedirect(continueParamValue);
    }
}
