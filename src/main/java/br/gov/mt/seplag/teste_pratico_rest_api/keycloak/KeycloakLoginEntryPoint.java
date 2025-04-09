package br.gov.mt.seplag.teste_pratico_rest_api.keycloak;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class KeycloakLoginEntryPoint implements AuthenticationEntryPoint
{

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException
    {
        // Limpa qualquer autenticação residual
        SecurityContextHolder.clearContext();

        // Força novo login no Keycloak
        response.sendRedirect("/oauth2/authorization/keycloak?prompt=login");
    }
}