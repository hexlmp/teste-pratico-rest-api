package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.io.IOException;
import java.net.URLEncoder;

public class KeycloakLogoutHandler implements LogoutHandler
{

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

            try {
                String keycloakLogoutUrl = "http://localhost:8080/realms/seplag-mt/protocol/openid-connect/logout" +
                        "?id_token_hint=" + oidcUser.getIdToken().getTokenValue() +
                        "&post_logout_redirect_uri=" +
                        URLEncoder.encode("http://localhost:8081/index.html", "UTF-8");

                response.sendRedirect(keycloakLogoutUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to logout from Keycloak", e);
            }
        }
    }
}
