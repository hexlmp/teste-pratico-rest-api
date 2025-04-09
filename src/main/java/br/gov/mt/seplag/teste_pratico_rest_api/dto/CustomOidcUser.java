package br.gov.mt.seplag.teste_pratico_rest_api.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOidcUser implements OidcUser {
    private final OidcUser originalUser;
    private final String newAccessToken;

    public CustomOidcUser(OidcUser originalUser, String newAccessToken) {
        this.originalUser = originalUser;
        this.newAccessToken = newAccessToken;
    }

    @Override
    public Map<String, Object> getClaims() {
        return originalUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return originalUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        // Retorna um novo OidcIdToken com o novo access token
        return new OidcIdToken(
                newAccessToken,
                originalUser.getIdToken().getIssuedAt(),
                originalUser.getIdToken().getExpiresAt(),
                originalUser.getIdToken().getClaims()
        );
    }

    @Override
    public String getName() {
        return originalUser.getName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return originalUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return originalUser.getAuthorities();
    }
}