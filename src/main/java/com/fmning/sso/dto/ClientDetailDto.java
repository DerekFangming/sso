package com.fmning.sso.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ClientDetailDto {
    private final String clientId;
    private final String scope;
    private final String authorizedGrantTypes;
    private final String redirectUri;
    private final Integer accessTokenValiditySeconds;
    private final Integer refreshTokenValiditySeconds;
}
