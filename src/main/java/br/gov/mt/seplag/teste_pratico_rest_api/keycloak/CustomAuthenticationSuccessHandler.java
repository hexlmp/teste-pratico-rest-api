package br.gov.mt.seplag.teste_pratico_rest_api.keycloak;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler
{

    public CustomAuthenticationSuccessHandler() {
        super();
        setAlwaysUseDefaultTargetUrl(true); // Sempre redireciona para a URL padrão
        setDefaultTargetUrl("/index.html"); // Define a página inicial como destino
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Você pode adicionar lógica adicional aqui se necessário
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
/*public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler
{
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException
    {
        SavedRequest savedRequest = (SavedRequest) request.getSession()
                .getAttribute("SPRING_SECURITY_SAVED_REQUEST");

        String redirectUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() : "/";
        if (!redirectUrl.contains("login/oauth2/code") && !redirectUrl.contains("logout")) response.sendRedirect(redirectUrl);
    }
}*/
