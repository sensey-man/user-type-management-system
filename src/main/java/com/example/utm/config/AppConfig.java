package com.example.utm.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class AppConfig {

    @Bean(name = "gson")
    public Gson gson() {
        return new Gson();
    }

    @Bean(name = "validator")
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}
