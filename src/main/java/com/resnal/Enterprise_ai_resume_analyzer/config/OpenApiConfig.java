package com.resnal.Enterprise_ai_resume_analyzer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise AI Resume Analyzer API")
                        .version("v1.0")
                        .description("Cloud-Native API for automated resume text extraction, PII data governance, and skill-gap evaluation.")
                        .contact(new Contact()
                                .name("Kinzal")
                                .email("kinzal.1826@gmail.com")));
    }
}