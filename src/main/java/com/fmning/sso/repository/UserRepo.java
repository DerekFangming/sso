package com.fmning.sso.repository;

import com.fmning.sso.domain.UserEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class UserRepo {

    public UserEntity get(String username) {
        return UserEntity.builder()
                .username(username)
                .password("$2a$10$9MTu6J1L3VFNFpV2gSErIOt9xMCCtPjgPi2xU00rt9Mx1.ph.95Py")
                .grantedAuthoritiesList(Collections.singletonList(new SimpleGrantedAuthority("some role")))
                .build();
    }
}
