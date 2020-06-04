package com.fmning.sso.controller;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @RequestMapping({ "/user", "/me" })
    public Principal user(Principal principal) {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication)principal;
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails)oAuth2Authentication.getDetails();
        oAuth2AuthenticationDetails.setDecodedDetails("abc");

        oAuth2Authentication.setDetails(oAuth2AuthenticationDetails);
        return oAuth2Authentication;
    }

}
