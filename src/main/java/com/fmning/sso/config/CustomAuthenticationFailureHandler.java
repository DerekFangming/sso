package com.fmning.sso.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String continueParamValue = UrlUtils.buildRequestUrl(request);

        if (request.getParameter("prompt") == null) {
            if (request.getQueryString() == null) {
                continueParamValue += "?prompt=error";
            } else {
                continueParamValue += "&prompt=error";
            }
        }
        response.sendRedirect(continueParamValue);
    }
}
