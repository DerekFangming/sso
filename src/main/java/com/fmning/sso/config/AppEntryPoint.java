package com.fmning.sso.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AppEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public AppEntryPoint(String appName) {
        super(appName);
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
                                                     AuthenticationException exception) {
        String continueParamValue = UrlUtils.buildRequestUrl(request);
        String query = request.getQueryString();
        String redirect = super.determineUrlToUseForThisRequest(request, response, exception);
//        return UriComponentsBuilder.fromPath(redirect).queryParam("app", continueParamValue).toUriString();
        return UriComponentsBuilder.fromPath(redirect).query(query).toUriString();
    }

}
