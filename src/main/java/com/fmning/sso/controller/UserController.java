package com.fmning.sso.controller;

import com.fmning.sso.domain.SsoUser;
import com.fmning.sso.domain.User;
import com.fmning.sso.dto.BaseDto;
import com.fmning.sso.dto.UserDto;
import com.fmning.sso.dto.VerificationCodeDto;
import com.fmning.sso.repository.UserRepo;
import com.fmning.sso.service.EmailService;
import com.fmning.sso.service.PasswordService;
import com.fmning.sso.service.SsoUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.Collection;

import static com.fmning.sso.controller.UiController.DEFAULT_AVATAR;

@RestController
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class UserController {

    private final UserRepo userRepo;
    private final PasswordService passwordService;
    private final EmailService emailService;
    private final SsoUserDetailsService userDetailsService;

    @GetMapping("/user")
    public Principal me(Principal principal) {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return principal;
    }

    @GetMapping("/user1")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void me1() {

    }

    @GetMapping("/user2")
    @PreAuthorize("hasAuthority('HAHA')")
    public void me2() {

    }

    @PostMapping("/api/signup")
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

    @PostMapping("/api/send-verification-email")
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

    @PostMapping("/api/send-recovery-email")
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

    @PostMapping("/api/reset-password")
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

    @PostMapping("/api/user/profile")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UserDto userDto) {
        SsoUser user = (SsoUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setDisplayName(userDto.getDisplayName());
        User savedUser = userRepo.findByUsername(user.getUsername());
        savedUser.setDisplayName(userDto.getDisplayName());

        if (DEFAULT_AVATAR.equals(userDto.getAvatar())) {
            savedUser.setAvatar(null);
        } else {
            savedUser.setAvatar(userDto.getAvatar());
        }

        userRepo.save(savedUser);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/api/user/password")
    public ResponseEntity<UserDto> updatePassword(@RequestBody UserDto userDto) {
        String username = ((SsoUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException("The username is not found.");
        } else if (!passwordService.isPasswordValid(userDto.getNewPassword())) {
            throw new IllegalArgumentException("Password does not meet the strength requirement.");
        } else if (!passwordService.matchesPassword(userDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("The password you entered does not match our record.");
        }

        user.setPassword(passwordService.encodePassword(userDto.getNewPassword()));
        userRepo.save(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/api/user/roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> updateRoles(@RequestBody UserDto userDto) {
        if (userDto.getId() <= 0) {
            throw new IllegalArgumentException("The user ID is invalid.");
        }
        User user = userRepo.findById(userDto.getId()).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("The user is not found.");
        }

        if (StringUtils.isBlank(userDto.getRoles())) {
            user.setRole(null);
        } else {
            user.setRole(userDto.getRoles().toUpperCase().trim());
        }
        userRepo.save(user);
        return ResponseEntity.ok(userDto);
    }


}
