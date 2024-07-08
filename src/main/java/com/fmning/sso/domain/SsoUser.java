package com.fmning.sso.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true, value = {"password"})
public class SsoUser extends org.springframework.security.core.userdetails.User {

    private String displayName;
    private String avatar;

    public SsoUser(User user) {
        super(user.getUsername(), user.getPassword(), user.isConfirmed(), true, true, true, user.getGrantedAuthorities());

        displayName = user.getDisplayName();
        avatar = user.getAvatar();
    }
}
