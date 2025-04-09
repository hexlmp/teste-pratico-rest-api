package br.gov.mt.seplag.teste_pratico_rest_api.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@Service
public class KeycloakTokenService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public KeycloakTokenService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    public String refreshAccessToken(OAuth2AuthenticationToken authentication) {
        String registrationId = authentication.getAuthorizedClientRegistrationId();
        String principalName = authentication.getName();

        // Obtém o cliente autorizado (contém o access_token e refresh_token)
        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(registrationId, principalName);

        if (authorizedClient == null) {
            throw new IllegalStateException("Cliente OAuth2 não encontrado!");
        }

        // O Spring Security faz o refresh automaticamente se o access_token expirar
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        return accessToken.getTokenValue();
    }
}