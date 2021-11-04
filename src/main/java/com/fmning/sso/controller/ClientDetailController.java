package com.fmning.sso.controller;

import com.fmning.sso.domain.ClientDetail;
import com.fmning.sso.dto.ClientDetailDto;
import com.fmning.sso.dto.UserDto;
import com.fmning.sso.repository.ClientDetailRepo;
import com.fmning.sso.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor_={@Autowired})
@RequestMapping("/client-details")
public class ClientDetailController {

    private final UserRepo userRepo;
    private final ClientDetailRepo clientDetailRepo;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientDetailDto> createClientDetail(@RequestBody ClientDetail clientDetail) {
        System.out.println(1);
        return ResponseEntity.ok(ClientDetailDto.builder()
                .clientId(clientDetail.getClientId())
                .scope(clientDetail.getScope())
                .authorizedGrantTypes(clientDetail.getAuthorizedGrantTypes())
                .redirectUri(clientDetail.getRedirectUri())
                .accessTokenValiditySeconds(clientDetail.getAccessTokenValiditySeconds())
                .refreshTokenValiditySeconds(clientDetail.getRefreshTokenValiditySeconds())
                .build());
    }
}
