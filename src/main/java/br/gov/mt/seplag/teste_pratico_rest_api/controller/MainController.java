package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController
{
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/access-denied-page")
    public String accessDenied() {
        return "access-denied"; // Retorna o template 'access-denied.html'
    }


}
