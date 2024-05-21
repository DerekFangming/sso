package com.fmning.sso.service;

import com.fmning.sso.domain.ClientDetail;
import com.fmning.sso.repository.ClientDetailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Component
public class SsoClientDetailsService implements RegisteredClientRepository {

    @Autowired
    private ClientDetailRepo clientDetailRepo;

    @Override
    public void save(RegisteredClient registeredClient) {
        System.out.println("Saving client");
        throw new IllegalStateException("Saving client is not implemented");
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
                .clientId(clientDetail.getClientId())
                .clientSecret(clientDetail.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantTypes(a -> {
                    a.addAll(splitByComma(clientDetail.getAuthorizedGrantTypes())
                            .stream().map(AuthorizationGrantType::new).collect(Collectors.toSet()));
                })
                .redirectUris(r -> {
                    r.addAll(splitByComma(clientDetail.getRedirectUri()));
                })
                .scopes(s -> {
                    s.addAll(splitByComma(clientDetail.getScope()));
                })
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofSeconds(clientDetail.getAccessTokenValiditySeconds()))
                        .refreshTokenTimeToLive(Duration.ofSeconds(clientDetail.getRefreshTokenValiditySeconds()))
                        .build());

        return registeredClient.build();
    }

    private List<String> splitByComma(String value) {
        String[] values = value.split(",");
        return new ArrayList<>(Arrays.asList(values));
    }
}
