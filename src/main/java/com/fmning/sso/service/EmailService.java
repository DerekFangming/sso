package com.fmning.sso.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmning.sso.SsoProperties;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class EmailService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final SsoProperties ssoProperties;

    public void sendResetPasswordEmail(String username, String displayName, String resetCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ").append(displayName).append(",\n")
                .append("Here is the verification code to reset your password. This code will expire in 24 hours.\n\n")
                .append(resetCode).append("\n\n")
                .append("Thank you,\n")
                .append("Support team");

        if (ssoProperties.isProduction()) {
            sendEmail(username, "Account reset code", sb.toString());
        } else {
            System.out.println(sb.toString());
        }
    }

    private void sendEmail(String recipient, String subject, String content) {
        Map<String, String> payload = new HashMap<>();
        payload.put("to", recipient);
        payload.put("subject", subject);
        payload.put("content", content);
        payload.put("senderType", "SEND_IN_BLUE");

        try {
            HttpPost httppost = new HttpPost("https://www.fmning.com/tools/api/email/send");

            StringEntity requestEntity = new StringEntity(objectMapper.writeValueAsString(payload), ContentType.APPLICATION_JSON);
            httppost.setEntity(requestEntity);
            httpClient.execute(httppost);
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
