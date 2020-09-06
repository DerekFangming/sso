package com.fmning.authentication.domain;

public class SsoUser extends org.springframework.security.core.userdetails.User {

    public SsoUser(User user) {
        super(user.getUsername(), user.getPassword(), user.isConfirmed(), true, true, true, user.getGrantedAuthorities());

    }
}
