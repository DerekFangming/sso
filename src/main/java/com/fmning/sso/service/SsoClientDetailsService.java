package com.fmning.sso.service;

import com.fmning.sso.domain.ClientDetail;
import com.fmning.sso.repository.ClientDetailRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

@Primary
@Component
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class SsoClientDetailsService implements ClientDetailsService {

    private final ClientDetailRepo clientDetailRepo;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

        ClientDetail clientDetail = clientDetailRepo.findById(clientId)
                .orElseThrow(() -> new ClientRegistrationException("client with id " + clientId + " not found"));

        BaseClientDetails baseClientDetails = new BaseClientDetails(clientDetail.getClientId(), null, clientDetail.getScope(),
                clientDetail.getAuthorizedGrantTypes(), null, clientDetail.getRedirectUri());
        baseClientDetails.setClientSecret(clientDetail.getClientSecret());
        baseClientDetails.setAccessTokenValiditySeconds(clientDetail.getAccessTokenValiditySeconds());
        baseClientDetails.setRefreshTokenValiditySeconds(clientDetail.getRefreshTokenValiditySeconds());
        baseClientDetails.setAutoApproveScopes(baseClientDetails.getScope());

        return baseClientDetails;
    }
}
