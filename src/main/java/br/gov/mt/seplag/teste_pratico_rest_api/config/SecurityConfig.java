package br.gov.mt.seplag.teste_pratico_rest_api.config;


import br.gov.mt.seplag.teste_pratico_rest_api.controller.KeycloakLogoutHandler;
import br.gov.mt.seplag.teste_pratico_rest_api.keycloak.CustomAuthenticationFailureHandler;

import br.gov.mt.seplag.teste_pratico_rest_api.keycloak.CustomAuthenticationSuccessHandler;
import br.gov.mt.seplag.teste_pratico_rest_api.keycloak.KeycloakLoginEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilitar para APIs stateless
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/protected/**").hasAnyRole("ADMIN", "USER")   // Endpoints de API protegidos
                        .requestMatchers("/private/**").hasRole("ADMIN")                       // Endpoints de API privativos ADM
                        .requestMatchers( "/public/**").permitAll()
                        .anyRequest().authenticated()
                        //.requestMatchers( "/**").authenticated()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .logoutUrl("/protected/api/session/logout") // Define o endpoint de logout
                        .addLogoutHandler(new KeycloakLogoutHandler())
                        .permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                        .authenticationEntryPoint(new KeycloakLoginEntryPoint())
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .failureHandler(new CustomAuthenticationFailureHandler())


                        //.defaultSuccessUrl("/", true)
                        //.failureUrl("/login?error=true")
                        //.loginPage("/oauth2/authorization/keycloak") // Ponto importante!
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().changeSessionId()
                )
                .exceptionHandling(handling -> handling
                        .accessDeniedPage("/access-denied-page")
                        .authenticationEntryPoint(new KeycloakLoginEntryPoint())
                )
                /*.exceptionHandling(handling -> handling
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/keycloak"))
                )*/
                .oauth2Client(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository())
                );;

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.<NimbusJwtDecoder>fromIssuerLocation("http://localhost:8080/realms/seplag-mt");
    }

    interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>>
    {}

    /*@Bean
    AuthoritiesConverter realmRolesAuthoritiesConverter() {
        return claims -> {
            var realmAccess = Optional.ofNullable((Map<String, Object>) claims.get("realm_access"));
            var roles = realmAccess.flatMap(map -> Optional.ofNullable((List<String>) map.get("roles")));
            return roles.map(List::stream)
                    .orElse(Stream.empty())
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                    .toList();
        };
    }*/
    @Bean
    AuthoritiesConverter realmRolesAuthoritiesConverter() {
        return claims -> {
            System.out.println("All claims in converter: " + claims); // Debug

            var realmAccess = Optional.ofNullable((Map<String, Object>) claims.get("realm_access"))
                    .orElse(Collections.emptyMap());

            var roles = Optional.ofNullable((List<String>) realmAccess.get("roles"))
                    .orElse(Collections.emptyList());

            return roles.stream()
                    .map(role -> "ROLE_" + role) // Adicione prefixo se necessário
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        };
    }



    @Bean
    GrantedAuthoritiesMapper authenticationConverter(
            Converter<Map<String, Object>, Collection<GrantedAuthority>> authoritiesConverter) {
        return (authorities) -> authorities.stream()
                .filter(authority -> authority instanceof OidcUserAuthority)
                .map(OidcUserAuthority.class::cast)
                .map(OidcUserAuthority::getIdToken)
                .map(OidcIdToken::getClaims)
                .map(authoritiesConverter::convert)
                .flatMap(roles -> roles.stream())
                .collect(Collectors.toSet());
    }
/*
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(
            Converter<Map<String, Object>, Collection<GrantedAuthority>> authoritiesConverter) {
        var authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            return authoritiesConverter.convert(jwt.getClaims());
        });
        return authenticationConverter;
    }*/

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientProvider authorizedClientProvider() {
        return OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .refreshToken(configurer -> configurer.clockSkew(Duration.ofMinutes(1))) // 1 min antes da expiração
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }



    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        // Configura o provider com refresh token mais agressivo
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken(configurer -> configurer.clockSkew(Duration.ofMinutes(1))) // 1 minuto de margem
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository,
                        authorizedClientRepository);

        // Configura um mapeador de atributos personalizado
        authorizedClientManager.setContextAttributesMapper(contextAttributesMapper());
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }


    private Function<OAuth2AuthorizeRequest, Map<String, Object>> contextAttributesMapper() {
        return authorizeRequest -> {
            Map<String, Object> contextAttributes = new HashMap<>();
            // Garante que o HttpServletRequest está disponível para o fluxo de refresh
            HttpServletRequest request = authorizeRequest.getAttribute(HttpServletRequest.class.getName());
            if (request != null) {
                contextAttributes.put(HttpServletRequest.class.getName(), request);
            }
            return contextAttributes;
        };
    }




    @Bean
    public ClientRegistrationRepository clientRegistrationRepository()
    {
        return new InMemoryClientRegistrationRepository(keycloakClientRegistration());
    }

    private ClientRegistration keycloakClientRegistration() {
        return ClientRegistration.withRegistrationId("keycloak")
                .clientName("Keycloak")
                .clientId("account")
                .clientSecret("XC1CxyVL5ca57ps60UoHQQ5eGtI7pOch")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                //.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                //.redirectUri("http://localhost:8081/*")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email", "roles")
                .userNameAttributeName("preferred_username")
                .authorizationUri("http://localhost:8080/realms/seplag-mt/protocol/openid-connect/auth")
                .tokenUri("http://localhost:8080/realms/seplag-mt/protocol/openid-connect/token")
                .userInfoUri("http://localhost:8080/realms/seplag-mt/protocol/openid-connect/userinfo")
                .jwkSetUri("http://localhost:8080/realms/seplag-mt/protocol/openid-connect/certs")
                .issuerUri("http://localhost:8080/realms/seplag-mt")
                .build();
    }

/*
    @Bean
    public FilterRegistrationBean<TokenExpirationFilter> tokenExpirationFilter(
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        FilterRegistrationBean<TokenExpirationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TokenExpirationFilter(authorizedClientRepository));
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 1); // Executa DEPOIS dos filtros do Spring Security
        return registration;
    }*/


}
