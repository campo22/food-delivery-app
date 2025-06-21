package com.diver.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Online Food Ordering Backend",
                version = "1.0",
                description = "Documentacion de la API para Sistema Full-Stack de Gestión y Pedido de Comida."
        )
)
public class openApiConfig {
}
