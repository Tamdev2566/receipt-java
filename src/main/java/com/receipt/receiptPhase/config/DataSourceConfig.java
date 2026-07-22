package com.receipt.receiptPhase.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    // 1. Primary Datasource Properties (Receipt)
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.receipt")
    public DataSourceProperties receiptDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "receiptDataSource")
    @Primary
    public DataSource receiptDataSource() {
        return receiptDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "receiptJdbcTemplate")
    @Primary
    public JdbcTemplate receiptJdbcTemplate(@Qualifier("receiptDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "namedParameterJdbcTemplate")
    @Primary
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Qualifier("receiptDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    // 2. Secondary Datasource Properties (User Auth)
    @Bean
    @ConfigurationProperties("spring.datasource.userauth")
    public DataSourceProperties userAuthDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "userAuthDataSource")
    public DataSource userAuthDataSource() {
        return userAuthDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "userAuthJdbcTemplate")
    public JdbcTemplate userAuthJdbcTemplate(@Qualifier("userAuthDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}