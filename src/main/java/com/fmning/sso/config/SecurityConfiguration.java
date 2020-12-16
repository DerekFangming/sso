package com.fmning.sso.config;

import com.fmning.sso.service.SsoUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletContext;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final ServletContext servletContext;
    private final SsoUserDetailsService customDetailsService;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customDetailsService).passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
                .authenticationEntryPoint(new AppEntryPoint("/login"))
            .and()
                .authorizeRequests()
                .antMatchers("/login", "/register", "/logout", "/encode-password/*", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .failureHandler(ssoAuthenticationFailureHandler())
            .and()
                .rememberMe()
                .tokenValiditySeconds(604800)
            .and()
                .logout()
                .logoutSuccessUrl("/login?prompt=logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            .and()
                .csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/reset-password", "/send-recovery-email");
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationFailureHandler ssoAuthenticationFailureHandler() {
        return new SsoAuthenticationFailureHandler(servletContext);
    }
}
