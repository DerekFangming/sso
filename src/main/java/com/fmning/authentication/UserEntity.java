package com.fmning.authentication;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@Builder
@Data
public class UserEntity {
    private String username;
    private String password;
    @Builder.Default
    private Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
}
