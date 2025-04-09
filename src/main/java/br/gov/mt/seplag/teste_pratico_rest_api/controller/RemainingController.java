package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;



@RestController
@RequestMapping("/public/api/session")
public class RemainingController
{
    private static final long WARNING_THRESHOLD = 120;

    @Autowired
    private OAuth2AuthorizedClientRepository authorizedClientRepository;

    @GetMapping("/remaining-time")
    public ResponseEntity<Map<String, Object>> getRemainingSessionTime(HttpServletRequest request /*OAuth2AuthenticationToken authentication*/) {
        try {
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
}
