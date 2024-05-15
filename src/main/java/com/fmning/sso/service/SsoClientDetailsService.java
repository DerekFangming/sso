package com.fmning.sso.service;

import com.fmning.sso.domain.ClientDetail;
import com.fmning.sso.repository.ClientDetailRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Primary
@Component
public class SsoClientDetailsService implements RegisteredClientRepository {

    @Autowired
    private ClientDetailRepo clientDetailRepo;

//    @Override
//    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
//
//        ClientDetail clientDetail = clientDetailRepo.findById(clientId)
//                .orElseThrow(() -> new ClientRegistrationException("client with id " + clientId + " not found"));
//
//        BaseClientDetails baseClientDetails = new BaseClientDetails(clientDetail.getClientId(), null, clientDetail.getScope(),
//                clientDetail.getAuthorizedGrantTypes(), null, clientDetail.getRedirectUri());
//        baseClientDetails.setClientSecret(clientDetail.getClientSecret());
//        baseClientDetails.setAccessTokenValiditySeconds(clientDetail.getAccessTokenValiditySeconds());
//        baseClientDetails.setRefreshTokenValiditySeconds(clientDetail.getRefreshTokenValiditySeconds());
//        baseClientDetails.setAutoApproveScopes(baseClientDetails.getScope());
//
//        return baseClientDetails;
//    }

    @Override
    public void save(RegisteredClient registeredClient) {
        System.out.println("Saving client");
    }

    @Override
    public RegisteredClient findById(String id) {
        return findByClientId(id);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        ClientDetail clientDetail = clientDetailRepo.findById(clientId)
                .orElseThrow(() -> new IllegalStateException("Client with id " + clientId + " not found"));

        RegisteredClient.Builder registeredClient = RegisteredClient.withId("tools")
                .clientId("tools")
//                .clientSecret("be50a6ad-eb14-4555-a52e-4b166a42d497")
                .clientSecret("$2a$10$Joa58Z6K8wS8oV0CkxkUtuWpi0psMv.DyoEVpFNkhq6MI7Vlo8laO")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .redirectUri("http://www.fmning.com/tools/login")
                .redirectUri("https://www.fmning.com/tools/login")
                .redirectUri("http://127.0.0.1:8080/tools/login")
//                .redirectUri("http://localhost:8080/tools/login")
                .scope("read")
                .scope("write")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofSeconds(clientDetail.getAccessTokenValiditySeconds()))
                        .refreshTokenTimeToLive(Duration.ofSeconds(clientDetail.getRefreshTokenValiditySeconds()))
                        .build());

        return registeredClient.build();
    }
}
