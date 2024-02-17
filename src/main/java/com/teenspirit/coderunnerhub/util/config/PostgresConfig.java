package com.teenspirit.coderunnerhub.util.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = PostgresConfig.JPA_REPOSITORY_PACKAGE,
        entityManagerFactoryRef = PostgresConfig.ENTITY_MANAGER_FACTORY,
        transactionManagerRef = PostgresConfig.TRANSACTION_MANAGER
)
public class PostgresConfig {
    public static final String JPA_REPOSITORY_PACKAGE = "com.teenspirit.coderunnerhub.repository.postgres";
    public static final String PROPERTY_PREFIX = "spring.data.postgres";
    public static final String DATA_SOURCE = "postgresDataSource";
    public static final String DATABASE_PROPERTY = "postgresDataBaseProperty";
    public static final String ENTITY_MANAGER_FACTORY = "postgresEntityManagerFactory";
    public static final String ENTITY_PACKAGE = "com.teenspirit.coderunnerhub.model.postgres";
    public static final String TRANSACTION_MANAGER = "postgresTransactionManager";

    @Value("${spring.data.postgres.username}")
    private String dataBaseUsername;

    @Value("${spring.data.postgres.password}")
    private String dataBasePassword;

    @Value("${spring.data.postgres.url}")
    private String dataBaseURL;

    @Value("${spring.data.postgres.driver-class-name}")
    private String dataBaseDriver;


    @Bean(name = DATABASE_PROPERTY)
    @ConfigurationProperties(prefix = PROPERTY_PREFIX)
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(DATA_SOURCE)
    public DataSource postgresDataSource(
            @Qualifier(DATABASE_PROPERTY) DataSourceProperties dataSourceProperties
    ) {
        return DataSourceBuilder
                .create()
                .username(dataBaseUsername)
                .password(dataBasePassword)
                .url(dataBaseURL)
                .driverClassName(dataBaseDriver)
                .build();
    }

    @Bean(name = ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(
            @Qualifier(DATA_SOURCE) DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource);
        entityManager.setPersistenceUnitName(ENTITY_MANAGER_FACTORY);
        entityManager.setPackagesToScan(ENTITY_PACKAGE);
        entityManager.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.validation.mode", "none");
        properties.put("hibernate.hbm2ddl.auto", "update");
        entityManager.setJpaPropertyMap(properties);
        return entityManager;
    }

    @Bean(name = TRANSACTION_MANAGER)
    public PlatformTransactionManager sqlSessionTemplate(
            @Qualifier(ENTITY_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean entityManager,
            @Qualifier(DATA_SOURCE) DataSource dataSource
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager.getObject());
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
