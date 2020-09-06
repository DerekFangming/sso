package com.fmning.sso.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="sso_users")
public class User {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name="username")
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="role")
    private String role;

    @Column(name="confirmed")
    private boolean confirmed;

    @Column(name="confirm_code")
    private String confirmCode;

    @Column(name="password_reset_code")
    private String passwordResetCode;

    @Column(name="created_at")
    private Instant createdAt;

    public Collection<GrantedAuthority> getGrantedAuthorities() {
        Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
        if (role != null) {
            for (String r : role.split(",")) {
                grantedAuthoritiesList.add(new SimpleGrantedAuthority(r));
            }
        }
        return grantedAuthoritiesList;
    }

}
