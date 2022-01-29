package com.fmning.sso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fmning.sso.util.ToLowerCaseDeserializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto extends BaseDto {
    private String displayName;

    @JsonDeserialize(using = ToLowerCaseDeserializer.class)
    private String username;
    private String avatar;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordResetCode;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String roles;
}
