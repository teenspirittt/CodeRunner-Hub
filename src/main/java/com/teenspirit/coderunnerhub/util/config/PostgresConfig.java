package com.teenspirit.coderunnerhub.util.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.teenspirit.coderunnerhub.repository.postgres",
       // entityManagerFactoryRef =
)
public class PostgresConfig {

}
