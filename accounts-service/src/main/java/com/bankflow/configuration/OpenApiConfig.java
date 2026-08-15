package com.bankflow.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BankFlow API")
                        .description("Motor de transferencias bancarias con idempotencia, auditoría y consistencia ACID")
                        .version("v1"));
    }
}