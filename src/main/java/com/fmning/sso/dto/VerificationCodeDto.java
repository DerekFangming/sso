package com.fmning.sso.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class VerificationCodeDto {
    String username;
    String code;
    Instant expiration;
}
