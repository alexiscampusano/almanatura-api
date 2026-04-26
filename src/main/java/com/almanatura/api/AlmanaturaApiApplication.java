package com.almanatura.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AlmanaturaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlmanaturaApiApplication.class, args);
    }
}
