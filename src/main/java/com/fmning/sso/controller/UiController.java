package com.fmning.sso.controller;

import com.fmning.sso.SsoProperties;
import com.fmning.sso.domain.SsoUser;
import com.fmning.sso.domain.User;
import com.fmning.sso.dto.VerificationCodeDto;
import com.fmning.sso.repository.ClientDetailRepo;
import com.fmning.sso.repository.UserRepo;
import com.fmning.sso.service.PasswordService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;

@Controller
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class UiController implements ErrorController {

    private final UserRepo userRepo;
    private final ClientDetailRepo clientDetailRepo;
    private final SsoProperties ssoProperties;
    private final ServletContext servletContext;
    private final PasswordService passwordService;

    public static final String DEFAULT_AVATAR = "https://i.imgur.com/lkAhvIs.png";

    @GetMapping("/")
    public String homePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SsoUser user = (SsoUser)authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
        if (!isAdmin) {
            return "redirect:/profile";
        }

        model.addAttribute("userList", userRepo.findAllByOrderByIdAsc());
        model.addAttribute("displayName", user.getDisplayName());
        return "userDashboard";
    }

    @GetMapping("/applications")
    public String applications(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SsoUser user = (SsoUser)authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
        if (!isAdmin) {
            return "redirect:/profile";
        }

        model.addAttribute("clientDetailList", clientDetailRepo.findAll());
        model.addAttribute("displayName", user.getDisplayName());
        return "applications";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        SsoUser user = (SsoUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = user.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));

        User savedUser = userRepo.findByUsername(user.getUsername());
        if (savedUser.getAvatar() == null) savedUser.setAvatar(DEFAULT_AVATAR);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("user", userRepo.findByUsername(user.getUsername()));
        model.addAttribute("uploadUrl", ssoProperties.isProduction() ? "https://tools.fmning.com/api/images" : "http://localhost:9101/api/images");
        return "profile";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        model.addAttribute("loginUrl", UrlUtils.buildRequestUrl(request));
        String clientId = request.getParameter("client_id");

        String title = "Fmning";
        String titleColor = "black";
        String titleBackgroundColor = "#e3f2fd";

        if (clientId != null) {
            switch (clientId) {
                case "yaofeng":
                    title = "Discord";
                    titleColor = "white";
                    titleBackgroundColor = "#343a40";
                    break;
                case "tools":
                    title = "Tools";
                    titleBackgroundColor = "#f8f9fa";
                    break;
                case "drive":
                    title = "Share Drive";
                    titleColor = "white";
                    titleBackgroundColor = "#007bff";
            }
        }

        model.addAttribute("titleText",title);
        model.addAttribute("titleColor", titleColor);
        model.addAttribute("titleBackgroundColor", titleBackgroundColor);

        if (request.getParameter("prompt") != null) {
            model.addAttribute("prompt", request.getParameter("prompt"));
        }

        return "login";
    }

    @GetMapping("/signup")
    public String register(Model model) {
        return "signup";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam(name = "code", required = false) String code, Model model) {
        if (code == null) {
            return "verify";
        } else {
            try {
                VerificationCodeDto dto = passwordService.decodeVerificationCode(code);
                User user = userRepo.findByUsername(dto.getUsername());
                if (user == null || user.isConfirmed() || user.getConfirmCode() == null || !user.getConfirmCode().equals(code)) {
                    throw new IllegalStateException("This account verification link you provided is not valid. Try resend verification email from login page.");
                } else if (dto.getExpiration().isBefore(Instant.now())) {
                    throw new IllegalArgumentException("This account verification link has expired. Try resend verification email from login page.");
                }

                user.setConfirmCode(null);
                user.setConfirmed(true);
                userRepo.save(user);

                model.addAttribute("verificationStatus", "success");
                return "verify";
            } catch (Exception e) {
                model.addAttribute("verificationStatus", "fail");
                model.addAttribute("errorMessage", e.getMessage());
                return "verify";
            }
        }
    }

    @GetMapping("/reset-password")
    public String resetPassword(Model model) {
        return "reset";
    }

    @GetMapping("/fake-error")
    public String fakeErr(Model model) {
        throw new IllegalStateException("wrong");
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        String errorMessage = "Internal error";

        if (message != null) {
            errorMessage = "";
            if (status != null) {
                errorMessage = status + ": ";
            }

            errorMessage += message;
        }
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

}
