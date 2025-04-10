package br.gov.mt.seplag.teste_pratico_rest_api.interceptor;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class TokenExpirationInterceptor implements HandlerInterceptor {

    @Value("${keycloak.auth-server-url}")
    private String keycloakHost;

    @Value("${app.host}")
    private String appHost;    // http://localhost:8081

    @Autowired
    private OAuth2AuthorizedClientRepository authorizedClientRepository;
    @Autowired
    private OAuth2AuthorizedClientManager authorizedClientManager;

    private final List<String> excludedPaths = List.of(
            "/login",
            "/oauth2/authorization/keycloak",
            "/logout",
            "/error",
            "/css/**",
            "/js/**"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // Ignora rotas excluídas
        String path = request.getRequestURI();
        if (excludedPaths.stream().anyMatch(path::startsWith)) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Permite passagem se não for autenticado (o Spring Security redirecionará)
        if (authentication == null || !authentication.isAuthenticated()) {
            return true;
        }

        //
        /*// Cria o request para autorização
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
                .principal(authentication)
                .attribute(HttpServletRequest.class.getName(), request)
                .build();
        // Isso irá automaticamente renovar o token se necessário
        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
         //*/

        OAuth2AuthorizedClient authorizedClient = authorizedClientRepository.loadAuthorizedClient("keycloak", authentication, request);

        if (authorizedClient != null) {
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

            // Verifica se o token está prestes a expirar (apenas adiciona header)
            //if (accessToken != null && accessToken.getExpiresAt().isBefore(Instant.now().plus(Duration.ofMinutes(5)))) {
            if (accessToken != null && accessToken.getExpiresAt() != null) {
                response.addHeader("X-AccessToken-Expire", ""+Duration.between(Instant.now(), accessToken.getExpiresAt()).toSeconds());
                response.addHeader("X-AccessToken-Principal", authorizedClient.getPrincipalName());
            }
            if (accessToken != null && accessToken.getExpiresAt().isBefore(Instant.now())) {
                //handleExpiredToken(request,response);
                //response.addHeader("X-AccessToken-Expire","Access Token expira em " + Duration.between(Instant.now(), accessToken.getExpiresAt()).toSeconds() + " s");
                request.logout();

                // Redireciona de volta após login
                response.sendRedirect(keycloakHost + "/realms/seplag-mt/protocol/openid-connect/logout?redirect_uri=" + appHost);
                return true;
            }


            try {
                // Decodificar o JWT
                JWT jwtToken = JWTParser.parse(refreshToken.getTokenValue());

                // Obter o conjunto de claims do JWT
                JWTClaimsSet claimsSet =  jwtToken.getJWTClaimsSet();

                // Acessar o atributo 'exp' (data de expiração)
                Date expirationDate = claimsSet.getExpirationTime();

                if (expirationDate != null) {
                    //if (expirationDate.toInstant().isBefore(Instant.now().plus(Duration.ofMinutes(5)))) {
                    response.addHeader("X-RefreshToken-Expire", "" + Duration.between(Instant.now(), expirationDate.toInstant()).toSeconds());
                    //}
                }

            } catch (Exception e) {
                System.err.println("Erro ao decodificar o Refresh JWT: " + e.getMessage());
            }



            // Não bloqueia requisições mesmo com token expirado - deixe o Spring Security lidar
        }

        return true;
    }

    private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // Limpa o contexto e sessão
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Redireciona para o Keycloak com prompt=login para forçar nova autenticação
        response.sendRedirect("/oauth2/authorization/keycloak?prompt=login");
    }
}