package com.decoder.course.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {
    // Força Spring a inicializar Flyway antes de qualquer bean JPA
    @Autowired
    public HibernateConfig(Flyway flyway) {
        // Flyway.migrate() já foi chamado pelo bean(initMethod="migrate")
        // Este autowire garante a dependência de ordem
    }
}
