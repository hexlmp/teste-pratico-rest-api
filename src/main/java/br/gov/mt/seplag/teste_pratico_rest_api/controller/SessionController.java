package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import br.gov.mt.seplag.teste_pratico_rest_api.service.SessionTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/protected/api/session")
public class SessionController {

    private static final long WARNING_THRESHOLD = 120;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final SessionTokenService sessionTokenService;
    //private final WebClientReactiveRefreshTokenTokenResponseClient refreshTokenClient;

    /*public SessionController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
        //this.refreshTokenClient = refreshTokenClient;
    }*/
    public SessionController(SessionTokenService sessionTokenService,  OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
        this.sessionTokenService = sessionTokenService;

    }

    @GetMapping("/user")
    public String getUser(OAuth2AuthenticationToken authentication) {
        if (authentication != null)
        {
            OidcUser user = (OidcUser) authentication.getPrincipal();
            String username = user.getName(); // Será o preferred_username
            return "Usuário: " + username;
        } else return "Não autenticado";
    }

    @GetMapping("/remaining-time")
    public ResponseEntity<Map<String, Object>> getRemainingSessionTime(HttpServletRequest request /*OAuth2AuthenticationToken authentication*/) {
        try {
            /*OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    authentication.getAuthorizedClientRegistrationId(),
                    authentication.getPrincipal().getName());

            if (authorizedClient == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "error", "Cliente OAuth2 não encontrado",
                        "success", false
                ));
            }
            long remainingTime = Objects.requireNonNull(authorizedClient.getAccessToken().getExpiresAt()).getEpochSecond() - (System.currentTimeMillis() / 1000);
*/

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
                return ResponseEntity.badRequest().build();
            }

            OAuth2AuthorizedClient authorizedClient = authorizedClientRepository.loadAuthorizedClient("keycloak", authentication, request);


            if (authorizedClient != null)
            {
                OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

                if (accessToken != null && accessToken.getExpiresAt() != null)
                {
                    long remainingTime = accessToken.getExpiresAt().getEpochSecond() - (System.currentTimeMillis() / 1000);

                    // Se o token já expirou
                    if (remainingTime <= 0)
                    {
                        return ResponseEntity.status(401).body(Map.of(
                                "error", "Access Token expirado",
                                "remainingTime", 0,
                                "success", false
                        ));
                    }

                    // Se está perto de expirar
                    boolean showWarning = remainingTime <= WARNING_THRESHOLD;

                    return ResponseEntity.ok(Map.of(
                            "remainingTime", remainingTime,
                            "showWarning", showWarning,
                            "principal", authorizedClient.getPrincipalName(),
                            "success", true
                    ));
                } else
                {
                    return ResponseEntity.status(401).body(Map.of(
                            "error", "getExpiresAt não localizado",
                            "remainingTime", 0,
                            "principal", authorizedClient.getPrincipalName(),
                            "success", false
                    ));
                }
            } else
            {
                return ResponseEntity.status(401).body(Map.of(
                        "error", "Access Token não localizado",
                        "remainingTime", 0,
                        "success", false
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", e.getMessage(),
                    "remainingTime", 0,
                    "success", false
            ));
        }
    }


    /*@GetMapping("/logout")
    public ResponseEntity<?> forceRefreshSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Força um novo login (redireciona para o Keycloak)
        //request.logout();

        // Redireciona de volta após login
        response.sendRedirect("http://localhost:8080/realms/seplag-mt/protocol/openid-connect/logout?redirect_uri=http://localhost:8081/oauth2/authorization/keycloak?prompt=login");
        return ResponseEntity.noContent().build();
    }*/



    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @AuthenticationPrincipal OidcUser principal) {

        // 1. Invalida a sessão local
        request.getSession().invalidate();

        // 2. Limpa os cookies
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath(request.getContextPath());
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // 3. Prepara a URL de logout do Keycloak
        if (principal != null) {
            String logoutUrl = "http://localhost:8080/realms/seplag-mt/protocol/openid-connect/logout" +
                    "?id_token_hint=" + principal.getIdToken().getTokenValue() +
                    "&post_logout_redirect_uri=" +
                    URLEncoder.encode("http://localhost:8081/index.html", StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header("Location", logoutUrl)
                    .body(Map.of("logoutUrl", logoutUrl));
        }

        return ResponseEntity.ok()
                .header("Location", "/index.html")
                .body(Map.of("logoutUrl", "/index.html"));
    }



    @PostMapping("/full-refresh")
    public ResponseEntity<?> fullRefresh(HttpServletRequest request) {
        // Invalida a sessão HTTP
        request.getSession().invalidate();

        // Redireciona para login
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/oauth2/authorization/keycloak")
                .build();
    }

    /*@GetMapping("/logout")
    @ResponseBody
    public String logout(@RequestParam String redirectUri) {
        // Aqui, você pode invalidar qualquer sessão interna ou estado no backend, se necessário.

        // Redireciona para o logout do Keycloak
        // URL do Keycloak para realizar o logout
        String keycloakLogoutUrl = "http://localhost:8080/realms/{realm}/protocol/openid-connect/logout";
        return "redirect:" + keycloakLogoutUrl + "?redirect_uri=" + redirectUri;
    }*/


    @Autowired
    private OAuth2AuthorizedClientManager authorizedClientManager;


    @Autowired
    private OAuth2AuthorizedClientRepository authorizedClientRepository;

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshAccessToken(HttpServletRequest request, HttpServletResponse response /*, OAuth2AuthenticationToken authentication*/) {

        try
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Cria o request para autorização
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
                    .principal(authentication)
                    .attribute(HttpServletRequest.class.getName(), request)
                    .build();

            // Isso irá automaticamente renovar o token se necessário
            OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

            if (authorizedClient == null)
            {
                return ResponseEntity.status(401).body(Map.of(
                        "error", "Cliente OAuth2 não encontrado",
                        "success", false
                ));
            }
            // authorizedClient = authorizedClientRepository.loadAuthorizedClient("keycloak", authentication, request);


            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

            if (accessToken == null)
            {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Access token não disponível",
                        "success", false
                ));
            }

            if (refreshToken == null)
            {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Refresh token não disponível",
                        "success", false
                ));
            }

            if (accessToken.getExpiresAt() == null)
            {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "getExpiresAt não encontrado no Access Token",
                        "success", false
                ));
            }

            Map<String, Object> retorno = new HashMap<String, Object>(Map.of(
                    "success", true,
                    "message", "Token renovado!"
            ));

            // Verifica se o token está prestes a expirar (apenas adiciona header)
            if (accessToken.getExpiresAt().isBefore(Instant.now().plus(Duration.ofMinutes(5))))
            {
                retorno.put("access_token", Duration.between(Instant.now(), accessToken.getExpiresAt()).toSeconds() + " s");
            }

            try
            {
                // Decodificar o JWT
                JWT jwtToken = JWTParser.parse(refreshToken.getTokenValue());
                // Obter o conjunto de claims do JWT
                JWTClaimsSet claimsSet = jwtToken.getJWTClaimsSet();
                // Acessar o atributo 'exp' (data de expiração)
                Date expirationDate = claimsSet.getExpirationTime();

                if (expirationDate != null)
                {
                    if (expirationDate.toInstant().isBefore(Instant.now().plus(Duration.ofMinutes(5))))
                    {
                        retorno.put("refresh_token", Duration.between(Instant.now(), expirationDate.toInstant()).toSeconds() + " s");
                    }
                }

            } catch (Exception e)
            {
                System.err.println("Erro ao decodificar o Refresh JWT: " + e.getMessage());
            }

            return ResponseEntity.ok(retorno);

            // Não bloqueia requisições mesmo com token expirado - deixe o Spring Security lidar
        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Erro ao efetuar refresh token",
                    "success", false
            ));
        }

    }



}