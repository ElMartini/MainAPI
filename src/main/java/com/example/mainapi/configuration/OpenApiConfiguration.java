package com.example.mainapi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {


    @Bean
    public OpenAPI defineOpenApi(){
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Main");


        Contact myContact = new Contact();
        myContact.email("test@gmail.com");
        myContact.setName("Marcin");

        Info info = new Info()
                .title("Main API")
                .version("v1")
                .description("API")
                .contact(myContact);

        return new OpenAPI().info(info).servers(List.of(server));
    }
}
