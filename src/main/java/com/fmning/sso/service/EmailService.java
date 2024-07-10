package com.fmning.sso.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmning.sso.SsoProperties;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class EmailService {

    private final ObjectMapper objectMapper;
    private final OkHttpClient client;
    private final SsoProperties ssoProperties;
    private final ServletContext servletContext;


    public boolean isEmailValid(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void sendConfirmAccountEmail(String username, String displayName, String confirmToken) {

        StringBuilder sb = new StringBuilder();
        sb.append("Hello ").append(displayName).append(",\n")
                .append("Here is the link to confirm your email address. This link will expire in 24 hours.\n\n")
                .append(ssoProperties.isProduction() ?  "https://sso.fmning.com/" : "http://localhost:9100/")
                .append("verify-email?code=")
                .append(confirmToken)
                .append("\n\n")
                .append("Thank you\n");

        if (ssoProperties.isProduction()) {
            sendEmail(username, "Confirm your account", sb.toString());
        } else {
            System.out.println(sb.toString());
        }
    }

    public void sendResetPasswordEmail(String username, String displayName, String resetCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ").append(displayName).append(",\n")
                .append("Here is the verification code to reset your password. This code will expire in 24 hours.\n\n")
                .append(resetCode)
                .append("\n\n")
                .append("Thank you\n");

        if (ssoProperties.isProduction()) {
            sendEmail(username, "Account reset code", sb.toString());
        } else {
            System.out.println(sb.toString());
        }
    }

    private void sendEmail(String recipient, String subject, String content) {
        JSONObject payload = new JSONObject();
        payload.put("to", recipient);
        payload.put("subject", subject);
        payload.put("content", content);
        payload.put("senderType", "SEND_IN_BLUE");

        try {
            Request request = new Request.Builder()
                    .url("https://tools.fmning.com/api/email/send")
                    .post(okhttp3.RequestBody.create(payload.toString(), MediaType.parse("application/json; charset=utf-8")))
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
