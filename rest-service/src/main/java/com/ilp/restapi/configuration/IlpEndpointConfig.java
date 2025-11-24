package com.ilp.restapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IlpEndpointConfig {
    @Bean
    public String ilpEndpoint() {
        String env = System.getenv("ILP_ENDPOINT");
        if (env == null || env.isBlank()) {
            return "https://ilp-rest-2025-bvh6e9hschfagrgy.ukwest-01.azurewebsites.net";
        }
        return env;
    }
}
