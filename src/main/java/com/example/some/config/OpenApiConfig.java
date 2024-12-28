package com.example.some.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library API")
                        .description("API documentation for Library Application")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Ivose")
                                .email("ivose@example.com")));
    }
}
