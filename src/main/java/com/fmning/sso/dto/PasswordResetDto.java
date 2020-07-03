package com.fmning.sso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordResetDto {
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordResetCode;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
