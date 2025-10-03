package ru.itmo.demography_service;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(
		servers = {
				@Server(url = "https://localhost:58124", description = "Local HTTPS")
		}
)
public class DemographyServiceApplication {
	public static void main(String[] args) {
//		Dotenv dotenv = Dotenv.configure()
//				.filename(".env")
//				.load();
//		dotenv.entries().forEach(entry ->
//				System.setProperty(entry.getKey(), entry.getValue())
//		);

		SpringApplication.run(DemographyServiceApplication.class, args);
	}
}
