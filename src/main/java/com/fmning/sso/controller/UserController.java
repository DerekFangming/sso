package com.fmning.sso.controller;

import com.fmning.sso.domain.User;
import com.fmning.sso.dto.BaseDto;
import com.fmning.sso.dto.UserDto;
import com.fmning.sso.dto.VerificationCodeDto;
import com.fmning.sso.repository.UserRepo;
import com.fmning.sso.service.EmailService;
import com.fmning.sso.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class UserController {

    private final UserRepo userRepo;
    private final PasswordService passwordService;
    private final EmailService emailService;

    @RequestMapping("/user")
    public Principal me(Principal principal) {
        return principal;
    }

    @PostMapping("/signup")
    public ResponseEntity<BaseDto> signUp(@RequestBody UserDto userDto) {

        if (StringUtils.isBlank(userDto.getDisplayName())) {
            throw new IllegalArgumentException("Display name must not be empty.");
        } else if (!emailService.isEmailValid(userDto.getUsername())) {
            throw new IllegalArgumentException("Username must be an email address.");
        } else if (!passwordService.isPasswordValid(userDto.getPassword())) {
            throw new IllegalArgumentException("Password does not meet the strength requirement.");
        }

        User user = userRepo.findByUsername(userDto.getUsername());
        if (user != null) {
            throw new IllegalArgumentException("This username is already registered.");
        }

        String confirmCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        String encryptedConfirmCode = passwordService.encodeVerificationCode(userDto.getUsername().trim(), confirmCode);
        User newUser = User.builder()
                .username(userDto.getUsername().trim())
                .password(passwordService.encodePassword(userDto.getPassword()))
                .displayName(userDto.getDisplayName().trim())
                .confirmCode(encryptedConfirmCode)
                .createdAt(Instant.now())
                .build();

        userRepo.save(newUser);
        emailService.sendConfirmAccountEmail(newUser.getUsername(), newUser.getDisplayName(), encryptedConfirmCode);

        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/send-verification-email")
    public ResponseEntity<UserDto> sendVerificationEmail(@RequestBody UserDto userDto) {
        User user = userRepo.findByUsername(userDto.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("The username is not found.");
        } else if (user.isConfirmed()) {
            throw new IllegalArgumentException("This account is already verified.");
        } else {
            String confirmCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
            String encryptedConfirmCode = passwordService.encodeVerificationCode(userDto.getUsername(), confirmCode);
            user.setConfirmCode(encryptedConfirmCode);
            userRepo.save(user);
            emailService.sendConfirmAccountEmail(user.getUsername(), user.getDisplayName(), encryptedConfirmCode);

            return ResponseEntity.ok(userDto);
        }
    }

    @PostMapping("/send-recovery-email")
    public ResponseEntity<UserDto> sendRecoveryEmail(@RequestBody UserDto userDto) {
        User user = userRepo.findByUsername(userDto.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("The username is not found.");
        } else {
            String resetCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
            user.setPasswordResetCode(passwordService.encodeVerificationCode(user.getUsername(), resetCode));
            userRepo.save(user);
            emailService.sendResetPasswordEmail(user.getUsername(), user.getDisplayName(), resetCode);

            return ResponseEntity.ok(userDto);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<UserDto> resetPassword(@RequestBody UserDto userDto) {
        User user = userRepo.findByUsername(userDto.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("The username is not found.");
        } else if (!passwordService.isPasswordValid(userDto.getPassword())) {
            throw new IllegalArgumentException("Password does not meet the strength requirement.");
        } else {
            VerificationCodeDto dto = passwordService.decodeVerificationCode(user.getPasswordResetCode());
            if (!dto.getCode().equalsIgnoreCase(userDto.getPasswordResetCode())) {
                throw new IllegalArgumentException("The recovery code does not match our record.");
            } else if (dto.getExpiration().isBefore(Instant.now())) {
                throw new IllegalArgumentException("The recovery code has expired. Please reset it again.");
            }

            user.setPasswordResetCode(null);
            user.setPassword(passwordService.encodePassword(userDto.getPassword()));
            userRepo.save(user);
            return ResponseEntity.ok(userDto);
        }
    }


}
