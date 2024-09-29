package com.d201.goodshot.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OpenAPIDefinition(
        servers = {
                @Server(url = "https://j11d201.p.ssafy.io", description = "개발 서버"),
                @Server(url = "http://localhost:8080", description = "로컬 서버")
        })
@Configuration
public class SwaggerConfig {

    private static final String JWT = "JWT";

    @Bean
    public OpenAPI openAPI() {
        // addList()에 추가된 항목들이 응답에 담겨서 나감
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(JWT);
        // components들을 swagger ui 페이지에서 설정가능
        Components components = new Components()
                .addSecuritySchemes(JWT, new SecurityScheme()
                        .name(JWT)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                );

        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private Info apiInfo() {
        return new Info()
                .title("GoodShot API")
                .description("This is the API for GoodShot Application")
                .version("1.0.0");
    }

}
