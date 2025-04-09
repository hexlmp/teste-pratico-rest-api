package br.gov.mt.seplag.teste_pratico_rest_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permitindo acesso apenas do domínio especificado
        registry.addMapping("/**")                        // Pode usar qualquer padrão de URL (/** permite todas as URLs)
                .allowedOrigins("http://localhost")                 // Substitua com o domínio autorizado
                .allowedMethods("GET", "POST", "PUT", "DELETE")     // Métodos permitidos
                .allowedHeaders("*")                                // Permite todos os cabeçalhos
                .allowCredentials(true);                            // Caso precise enviar cookies, autoriza
    }
}
