package com.receipt.receiptPhase.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
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
        basePackages = "com.receipt.receiptPhase.repository",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.receipt\\.receiptPhase\\.repository\\.auth\\..*"
        ),
        entityManagerFactoryRef = "receiptEntityManagerFactory",
        transactionManagerRef = "receiptTransactionManager"
)
public class ReceiptDbConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.receipt")
    public DataSourceProperties receiptDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "receiptDataSource")
    public DataSource receiptDataSource() {
        return receiptDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "receiptEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean receiptEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("receiptDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.receipt.receiptPhase.model")
                .persistenceUnit("receipt")
                .build();
    }

    @Primary
    @Bean(name = "receiptTransactionManager")
    public PlatformTransactionManager receiptTransactionManager(
            @Qualifier("receiptEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Primary
    @Bean(name = "receiptJdbcTemplate")
    public JdbcTemplate receiptJdbcTemplate(@Qualifier("receiptDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Primary
    @Bean(name = "namedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Qualifier("receiptDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}