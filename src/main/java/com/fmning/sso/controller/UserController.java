package com.fmning.sso.controller;

import com.fmning.sso.domain.User;
import com.fmning.sso.dto.PasswordResetDto;
import com.fmning.sso.repository.UserRepo;
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

@RestController
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class UserController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

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
            user.setPasswordResetCode(RandomStringUtils.randomAlphanumeric(6).toUpperCase());
            System.out.println(user.getPasswordResetCode());// TODO email
            userRepo.save(user);
            return ResponseEntity.ok(passwordResetDto);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetDto> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        User user = userRepo.findByUsername(passwordResetDto.getUsername());
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else if (StringUtils.isEmpty(passwordResetDto.getPasswordResetCode()) || !passwordResetDto.getPasswordResetCode().equals(user.getPasswordResetCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(passwordResetDto);
        } else if (StringUtils.isEmpty(passwordResetDto.getPassword()) || passwordResetDto.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body(passwordResetDto);
        } else {
//            user.setPasswordResetCode(RandomStringUtils.randomAlphanumeric(6).toUpperCase());
//            System.out.println(user.getPasswordResetCode());
//            userRepo.save(user);

            user.setPassword(passwordEncoder.encode(passwordResetDto.getPassword()));
            userRepo.save(user);
            return ResponseEntity.ok(passwordResetDto);
        }
    }


}
