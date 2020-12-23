package com.fmning.sso.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fmning.sso.dto.VerificationCodeDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Base64;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class PasswordService {

//    private final ObjectMapper objectMapper;
    private ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void setup() {

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(javaTimeModule);
    }

    public boolean isPasswordValid(String password) {
        return !StringUtils.isBlank(password) && password.length() >= 6;
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public String encodeVerificationCode(String username, String code) {
        VerificationCodeDto verificationCodeDto = new VerificationCodeDto(username, code, Instant.now().plus(1, ChronoUnit.DAYS));
        try {
            String json = objectMapper.writeValueAsString(verificationCodeDto);
            byte[] jsonBytes = Base64.encodeBase64(json.getBytes());
            return new String(jsonBytes);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public VerificationCodeDto decodeVerificationCode(String code) {
        byte[] jsonBytes = Base64.decodeBase64(code);

        try {
            return objectMapper.readValue(new String(jsonBytes), VerificationCodeDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
