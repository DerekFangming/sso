package com.fmning.sso.service;

import com.fmning.sso.domain.User;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendResetPasswordEmail(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ");
    }
}
