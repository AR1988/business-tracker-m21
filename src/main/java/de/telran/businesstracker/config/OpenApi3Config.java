package de.telran.businesstracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApi3Config {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Business tracker")
                .description("This is project of students Tel-Ran.de college")
                .version("1.0")
                .contact(apiContact())
                .license(null);
    }

    private Contact apiContact() {
        return new Contact()
                .name("Tel-Ran.de")
                .email("go@tel-ran.de")
                .url("https://www.tel-ran.de");
    }
}
