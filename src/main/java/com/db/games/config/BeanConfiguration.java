package com.db.games.config;

import com.db.games.service.ApiErrorHandler;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAutoConfiguration
public class BeanConfiguration {

    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ApiErrorHandler());
        return restTemplate;
    }

    @EnableScheduling
    public class SchedulingConfiguration {
    }
}
