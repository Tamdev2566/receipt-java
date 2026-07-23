package com.receipt.receiptPhase.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.receipt.receiptPhase.repository.auth",
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager"
)
public class AuthDbConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.userauth")
    public DataSourceProperties userAuthDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "userAuthDataSource")
    public DataSource userAuthDataSource() {
        return userAuthDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "authEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("userAuthDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.receipt.receiptPhase.model.auth")
                .persistenceUnit("auth")
                .build();
    }

    @Bean(name = "authTransactionManager")
    public PlatformTransactionManager authTransactionManager(
            @Qualifier("authEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    // --- Added Missing JdbcTemplates required by ChequeRepository ---
    @Bean(name = "userAuthJdbcTemplate")
    public JdbcTemplate userAuthJdbcTemplate(@Qualifier("userAuthDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "userAuthNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate userAuthNamedParameterJdbcTemplate(@Qualifier("userAuthDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}