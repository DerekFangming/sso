package com.fmning.sso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@RequiredArgsConstructor
public class ClientDetailDto {
    private String clientId;
    private String clientSecret;
    private String authorizedGrantTypes;
    private String redirectUri;
    private int accessTokenValiditySeconds;
    private int refreshTokenValiditySeconds;
}
