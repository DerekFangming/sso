package com.fmning.sso.controller;

import com.fmning.sso.domain.ClientDetail;
import com.fmning.sso.dto.ClientDetailDto;
import com.fmning.sso.repository.ClientDetailRepo;
import com.fmning.sso.repository.UserRepo;
import com.fmning.sso.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor(onConstructor_={@Autowired})
@RequestMapping("/client-details")
public class ClientDetailController {

    private final ClientDetailRepo clientDetailRepo;
    private final PasswordService passwordService;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientDetailDto> createClientDetail(@RequestBody ClientDetailDto clientDetailDto) {
        if (clientDetailDto.getAccessTokenValiditySeconds() < 60 || clientDetailDto.getRefreshTokenValiditySeconds() < 60) {
            throw new IllegalArgumentException("Token validity time has to be greater than 60 (1 min).");
        } else if (StringUtils.isEmpty(clientDetailDto.getAuthorizedGrantTypes())) {
            throw new IllegalArgumentException("Grant type cannot be empty.");
        } else if (StringUtils.isEmpty(clientDetailDto.getClientId())) {
            throw new IllegalArgumentException("Client ID type cannot be empty.");
        }

        clientDetailRepo.findById(clientDetailDto.getClientId()).ifPresent(c -> {
            throw new IllegalArgumentException("Client ID already exists: " + c.getClientId());
        });

        String clientSecret = UUID.randomUUID().toString();
        ClientDetail clientDetail = ClientDetail.builder()
                .clientId(clientDetailDto.getClientId())
                .clientSecret(passwordService.encodePassword(clientSecret))
                .scope("read,write")
                .authorizedGrantTypes(clientDetailDto.getAuthorizedGrantTypes())
                .redirectUri(clientDetailDto.getRedirectUri())
                .accessTokenValiditySeconds(clientDetailDto.getAccessTokenValiditySeconds())
                .refreshTokenValiditySeconds(clientDetailDto.getRefreshTokenValiditySeconds())
                .build();

        clientDetailRepo.save(clientDetail);

        clientDetailDto.setClientSecret(clientSecret);
        return ResponseEntity.ok(clientDetailDto);
    }

    @PutMapping("/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientDetailDto> updateClientDetail(@PathVariable(value="clientId") String clientId, @RequestBody ClientDetailDto clientDetailDto) {
        if (clientDetailDto.getAccessTokenValiditySeconds() < 60 || clientDetailDto.getRefreshTokenValiditySeconds() < 60) {
            throw new IllegalArgumentException("Token validity time has to be greater than 60 (1 min).");
        } else if (StringUtils.isEmpty(clientDetailDto.getAuthorizedGrantTypes())) {
            throw new IllegalArgumentException("Grant type cannot be empty.");
        }

        Optional<ClientDetail> clientDetailOptional = clientDetailRepo.findById(clientId);
        if (!clientDetailOptional.isPresent()) {
            throw new IllegalArgumentException("Client ID does not exist: " + clientId);
        }

        ClientDetail clientDetail = clientDetailOptional.get();
        clientDetail.setAuthorizedGrantTypes(clientDetailDto.getAuthorizedGrantTypes());
        clientDetail.setRedirectUri(clientDetailDto.getRedirectUri());
        clientDetail.setAccessTokenValiditySeconds(clientDetailDto.getAccessTokenValiditySeconds());
        clientDetail.setRefreshTokenValiditySeconds(clientDetailDto.getRefreshTokenValiditySeconds());

        clientDetailRepo.save(clientDetail);

        return ResponseEntity.ok(clientDetailDto);
    }
}
