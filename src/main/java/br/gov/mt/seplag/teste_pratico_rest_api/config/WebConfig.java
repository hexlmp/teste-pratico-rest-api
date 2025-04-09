package br.gov.mt.seplag.teste_pratico_rest_api.config;

import br.gov.mt.seplag.teste_pratico_rest_api.interceptor.TokenExpirationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenExpirationInterceptor tokenExpirationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenExpirationInterceptor)
                .order(Ordered.LOWEST_PRECEDENCE) // Executa depois dos filtros de segurança
                .addPathPatterns("/**")
                .excludePathPatterns("/login/**", "/oauth2/**", "/error", "/static/**");
    }

}