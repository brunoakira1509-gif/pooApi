package com.pontec.vagas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pontecVagasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pontec Vagas - API de Vagas de Emprego")
                        .description("API RESTful do portal de vagas Pontec Vagas. "
                                + "Permite cadastrar, consultar, atualizar e remover vagas de emprego.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Pontec Vagas")
                                .email("contato@pontecvagas.com"))
                        .license(new License()
                                .name("Projeto acadêmico - Engenharia de Software")))
                .servers(List.of(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor local de desenvolvimento")));
    }
}
