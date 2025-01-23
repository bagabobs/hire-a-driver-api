package com.baga.usermanagementservice.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.lang.NonNullApi;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
public class DatabaseConfiguration extends AbstractR2dbcConfiguration {
    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions options =  ConnectionFactoryOptions.builder()
                .option(DRIVER, "postgresql")
                .option(HOST, "localhost")
                .option(PORT, 5432)
                .option(USER, "hide")
                .option(PASSWORD, "hide")
                .option(DATABASE, "userdb")
                .build();
        return ConnectionFactories.get(options);
    }

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}
