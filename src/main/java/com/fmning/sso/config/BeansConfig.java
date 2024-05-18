package com.fmning.sso.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fmning.sso.service.SsoClientDetailsService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.TimeZone;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class BeansConfig {

    private final ServletContext servletContext;
    private final AuthenticationManagerBuilder auth;
    private final UserDetailsService userDetailsService;
    private final SsoClientDetailsService ssoClientDetailsService;
    private final AuthorizationServerConfig authorizationServerConfig;

    @PostConstruct
    private void init() throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    //@Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder
                .json()
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
//
        http
                .exceptionHandling((handler)-> handler.authenticationEntryPoint(new AppEntryPoint("/login")))

                .authorizeHttpRequests((requests) -> requests.requestMatchers("/login", "/signup", "/logout", "/encode-password/*", //"/verify-email",
                                "/favicon.ico", "/reset-password", "/send-recovery-email", "/send-verification-email", "/test", "/oauth/authorize").permitAll()
                .anyRequest().authenticated()
                )

                .formLogin((form) -> form.loginPage("/login").permitAll().failureHandler(ssoAuthenticationFailureHandler()))
                .logout((logout) -> logout.logoutUrl("/logout").permitAll())
                .csrf((csrf) -> csrf.disable())
                .httpBasic(withDefaults())
                .oauth2ResourceServer((resourceServer) -> resourceServer.jwt(withDefaults()))
        ;

        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler ssoAuthenticationFailureHandler() {
        return new SsoAuthenticationFailureHandler(servletContext);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private static RSAKey generateRsa() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        String a = new String(publicKey.getEncoded(), StandardCharsets.UTF_8);
        String b = new String(privateKey.getEncoded(), StandardCharsets.UTF_8);
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private static KeyPair generateRsaKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
