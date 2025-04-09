package br.gov.mt.seplag.teste_pratico_rest_api.service;

import br.gov.mt.seplag.teste_pratico_rest_api.dto.CustomOidcUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SessionTokenService {

    public void updateSessionToken(String newAccessToken) {
        // 1. Obter autenticação atual
        var authentication = (OAuth2AuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        // 2. Criar novo usuário com token atualizado
        OidcUser originalUser = (OidcUser) authentication.getPrincipal();
        //OidcUser updatedUser = new CustomOidcUser(originalUser, newAccessToken);

        // Cria um novo OidcIdToken com os novos valores
        OidcIdToken newIdToken = new OidcIdToken(
                newAccessToken,
                Instant.now(),
                Instant.now().plusSeconds(60),
                originalUser.getIdToken().getClaims()
        );

        // Cria um novo usuário OIDC com todas as informações atualizadas
        OidcUser updatedUser = new DefaultOidcUser(
                originalUser.getAuthorities(),
                newIdToken,
                originalUser.getUserInfo(),
                "preferred_username"
        );

        // 3. Criar nova autenticação
        var newAuthentication = new OAuth2AuthenticationToken(
                updatedUser,
                authentication.getAuthorities(),
                authentication.getAuthorizedClientRegistrationId()
        );


        // 4. Atualizar contexto de segurança
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
}