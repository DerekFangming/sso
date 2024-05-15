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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.TimeZone;
import java.util.UUID;

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

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();
        http.apply(authorizationServerConfigurer);

//        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
//                new OAuth2AuthorizationServerConfigurer();
        RequestMatcher endpointsMatcher = authorizationServerConfigurer
                .getEndpointsMatcher();

        http
                .exceptionHandling()
                .authenticationEntryPoint(new AppEntryPoint("/login"))
            .and()
                .formLogin()
                .failureHandler(ssoAuthenticationFailureHandler())

            .and()
                .csrf().disable()
                .securityMatcher(endpointsMatcher)
                //.csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .authorizeHttpRequests((authorize) ->
                        authorize
                        .requestMatchers("/login", "/signup", "/logout", "/encode-password/*", "/verify-email",
                                "/favicon.ico", "/reset-password", "/send-recovery-email", "/send-verification-email", "/test").permitAll()
                        .anyRequest().authenticated()
                )
        ;
//                .apply(authorizationServerConfigurer);
//        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
//            .oauth2Login()
//                .clientRegistrationRepository(new ClientRegistrationRepository() {
//                    @Override
//                    public ClientRegistration findByRegistrationId(String registrationId) {
//                        return null;
//                    }
//                })
//                .authorizationEndpoint().baseUri("/oauth/authorize")
//                .and().and().build();

        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler ssoAuthenticationFailureHandler() {
        return new SsoAuthenticationFailureHandler(servletContext);
    }

//    @Bean
//    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
//        RSAKey rsaKey = generateRsa();
//        JWKSet jwkSet = new JWKSet(rsaKey);
//        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
//    }
//
//    private static RSAKey generateRsa() throws NoSuchAlgorithmException {
//        KeyPair keyPair = generateRsaKey();
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//        return new RSAKey.Builder(publicKey)
//                .privateKey(privateKey)
//                .keyID(UUID.randomUUID().toString())
//                .build();
//    }
//
//    private static KeyPair generateRsaKey() throws NoSuchAlgorithmException {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(2048);
//        return keyPairGenerator.generateKeyPair();
//    }
}
