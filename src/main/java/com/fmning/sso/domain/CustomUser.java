package com.fmning.sso.domain;

import com.fmning.sso.domain.UserEntity;
import org.springframework.security.core.userdetails.User;

public class CustomUser extends User {
    private static final long serialVersionUID = 1L;
    public CustomUser(UserEntity user) {
        super(user.getUsername(), user.getPassword(), user.getGrantedAuthoritiesList());
    }
}
