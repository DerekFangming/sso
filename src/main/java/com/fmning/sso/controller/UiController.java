package com.fmning.sso.controller;

import com.fmning.sso.domain.SsoUser;
import com.fmning.sso.domain.User;
import com.fmning.sso.dto.VerificationCodeDto;
import com.fmning.sso.repository.UserRepo;
import com.fmning.sso.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@Controller
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class UiController {

    private final UserRepo userRepo;
    private final ServletContext servletContext;
    private final PasswordService passwordService;

    @GetMapping("/")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SsoUser user = (SsoUser)authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
        if (!isAdmin) {
            return "redirect:/profile";
        }

        model.addAttribute("userList", userRepo.findAllByOrderByIdAsc());
        model.addAttribute("displayName", user.getDisplayName());
        model.addAttribute("contextPath", servletContext.getContextPath());
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        SsoUser user = (SsoUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = user.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));

        model.addAttribute("idAdmin", isAdmin);
        model.addAttribute("user", user);
        model.addAttribute("contextPath", servletContext.getContextPath());
        return "profile";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        model.addAttribute("contextPath", servletContext.getContextPath());
        model.addAttribute("loginUrl", UrlUtils.buildRequestUrl(request));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String clientId = request.getParameter("client_id");
        return "login";
    }
    @GetMapping("/signup")
    public String register(Model model) {
        model.addAttribute("contextPath", servletContext.getContextPath());
        return "signup";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam(name = "code", required = false) String code, Model model) {
        model.addAttribute("contextPath", servletContext.getContextPath());
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
        model.addAttribute("contextPath", servletContext.getContextPath());
        return "reset";
    }

}
