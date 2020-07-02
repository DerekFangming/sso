package com.fmning.sso.controller;

import com.fmning.sso.domain.User;
import com.fmning.sso.dto.PasswordResetDto;
import com.fmning.sso.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class UserController {

    private final UserRepo userRepo;

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
            return ResponseEntity.ok(passwordResetDto);
        }
    }


}
