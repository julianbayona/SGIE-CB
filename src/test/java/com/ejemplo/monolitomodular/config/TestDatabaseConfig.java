package com.ejemplo.monolitomodular.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configuración de base de datos para tests.
 * Usa H2 en memoria e desactiva Flyway explícitamente.
 */
@Configuration
@Profile("test")
public class TestDatabaseConfig {

    /**
     * Crea un DataSource H2 en memoria para tests.
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        config.setDriverClassName("org.h2.Driver");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        return new HikariDataSource(config);
    }

    /**
     * Desactiva Flyway en perfil de test.
     */
    @Bean
    @ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "false")
    public Flyway flyway() {
        return null;
    }
}
