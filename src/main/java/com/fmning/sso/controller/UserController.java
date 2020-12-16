package com.fmning.sso.controller;

import com.fmning.sso.domain.User;
import com.fmning.sso.dto.PasswordResetDto;
import com.fmning.sso.dto.VerificationCodeDto;
import com.fmning.sso.repository.UserRepo;
import com.fmning.sso.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Instant;

@RestController
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class UserController {

    private final UserRepo userRepo;
    private final PasswordService passwordService;

    @RequestMapping("/user")
    public Principal me(Principal principal) {
        return principal;
    }

    @PostMapping("/send-recovery-email")
    public ResponseEntity<PasswordResetDto> sendRecoveryEmail(@RequestBody PasswordResetDto passwordResetDto) {
        User user = userRepo.findByUsername(passwordResetDto.getUsername());
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            String resetCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
            user.setPasswordResetCode(passwordService.encodeVerificationCode(user.getUsername(), resetCode));
            System.out.println(resetCode);// TODO email
            userRepo.save(user);
            return ResponseEntity.ok(passwordResetDto);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetDto> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        User user = userRepo.findByUsername(passwordResetDto.getUsername());
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else if (!passwordService.isPasswordValid(passwordResetDto.getPassword())) {
            return ResponseEntity.badRequest().body(passwordResetDto);
        } else {
            VerificationCodeDto dto = passwordService.decodeVerificationCode(user.getPasswordResetCode());
            if (!dto.getCode().equalsIgnoreCase(passwordResetDto.getPasswordResetCode())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(passwordResetDto);
            } else if (dto.getExpiration().isBefore(Instant.now())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(passwordResetDto);
            }

            user.setPasswordResetCode(null);
            user.setPassword(passwordService.encodePassword(passwordResetDto.getPassword()));
            userRepo.save(user);
            return ResponseEntity.ok(passwordResetDto);
        }
    }


}
