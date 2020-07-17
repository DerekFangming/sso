package com.fmning.sso.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="sso_client_details")
public class ClientDetail {

    @Id
    @Column(name="client_id")
    private String clientId;

    @Column(name="client_secret")
    private String clientSecret;

    @Column(name="scope")
    private String scope;

    @Column(name="authorized_grant_types")
    private String authorizedGrantTypes;

    @Column(name="redirect_uri")
    private String redirectUri;

    @Column(name="access_token_validity_seconds")
    private Integer accessTokenValiditySeconds;

    @Column(name="refresh_token_validity_seconds")
    private Integer refreshTokenValiditySeconds;
}
