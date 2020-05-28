package com.fmning.sso.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@Builder
@Data
public class User {
    private String username;
    private String password;
    @Builder.Default
    private Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
}
