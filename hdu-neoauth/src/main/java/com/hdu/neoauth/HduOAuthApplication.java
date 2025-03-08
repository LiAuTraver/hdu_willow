package com.hdu.neoauth;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class HduOAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(HduOAuthApplication.class, args);
    }
}

@RestController
class HomeController {
    @GetMapping("/")
    public String home() {
        return "Hello, World!";
    }

    @GetMapping("/secured")
    public String secured() {
        return "Hello, Secured!";
    }

    @GetMapping("/oauth2/authorization/github")
    public String oauth2() {
        return "Success!";
    }
}

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated()).oauth2Login().and().formLogin();

        return http.build();
    }
}
