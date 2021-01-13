package com.fmning.sso.domain;

import lombok.Getter;

@Getter
public class SsoUser extends org.springframework.security.core.userdetails.User {

    private String displayName;
    private String avatar;
    private String readableRoles;

    public SsoUser(User user) {
        super(user.getUsername(), user.getPassword(), user.isConfirmed(), true, true, true, user.getGrantedAuthorities());

        displayName = user.getDisplayName();
        avatar = user.getAvatar();
        readableRoles = user.getRole() == null ? "" : user.getRole().replaceAll(",", ", ");
    }
}
