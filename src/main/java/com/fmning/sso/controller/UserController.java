package com.fmning.sso.controller;

import com.fmning.sso.domain.User;
import com.fmning.sso.dto.UserDto;
import com.fmning.sso.dto.VerificationCodeDto;
import com.fmning.sso.repository.UserRepo;
import com.fmning.sso.service.EmailService;
import com.fmning.sso.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final EmailService emailService;

    @RequestMapping("/user")
    public Principal me(Principal principal) {
        return principal;
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody UserDto userDto) {

        if (!emailService.isEmailValid(userDto.getUsername())) {
            throw new IllegalArgumentException("abc");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username must be an email address.");
        } else if (!passwordService.isPasswordValid(userDto.getPassword())) {
            return ResponseEntity.badRequest().body(userDto);
        }

        User user = userRepo.findByUsername(userDto.getUsername());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        System.out.println(userDto.getDisplayName());
        System.out.println(userDto.getPassword());
        System.out.println(userDto.getUsername());
        if (userDto.getDisplayName().equals("a")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken.");
        }
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/send-recovery-email")
    public ResponseEntity<UserDto> sendRecoveryEmail(@RequestBody UserDto userDto) {
        User user = userRepo.findByUsername(userDto.getUsername());
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            String resetCode = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
            user.setPasswordResetCode(passwordService.encodeVerificationCode(user.getUsername(), resetCode));
            userRepo.save(user);
            emailService.sendResetPasswordEmail(user.getUsername(), "displayname", resetCode);// todo

            return ResponseEntity.ok(userDto);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<UserDto> resetPassword(@RequestBody UserDto userDto) {
        User user = userRepo.findByUsername(userDto.getUsername());
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else if (!passwordService.isPasswordValid(userDto.getPassword())) {
            return ResponseEntity.badRequest().body(userDto);
        } else {
            VerificationCodeDto dto = passwordService.decodeVerificationCode(user.getPasswordResetCode());
            if (!dto.getCode().equalsIgnoreCase(userDto.getPasswordResetCode())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userDto);
            } else if (dto.getExpiration().isBefore(Instant.now())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userDto);
            }

            user.setPasswordResetCode(null);
            user.setPassword(passwordService.encodePassword(userDto.getPassword()));
            userRepo.save(user);
            return ResponseEntity.ok(userDto);
        }
    }


}
