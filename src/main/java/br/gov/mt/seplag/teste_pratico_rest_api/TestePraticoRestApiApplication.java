package br.gov.mt.seplag.teste_pratico_rest_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.TimeZone;

@SpringBootApplication
@ComponentScan(basePackages = "br.gov.mt.seplag")
public class TestePraticoRestApiApplication {

	public static void main(String[] args)
	{
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(TestePraticoRestApiApplication.class, args);
	}

}
