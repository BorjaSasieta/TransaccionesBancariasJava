package com.bankflow.configuration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JPAConfigTest {

    @Test
    void jpa_config_class_exists() {
        assertThat(JPAConfig.class).isNotNull();
    }

    @Test
    void jpa_config_has_configuration_annotation() {
        assertThat(JPAConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class))
            .as("JPAConfig should have @Configuration annotation")
            .isTrue();
    }

    @Test
    void jpa_config_has_entity_scan() {
        assertThat(JPAConfig.class.isAnnotationPresent(org.springframework.boot.autoconfigure.domain.EntityScan.class))
            .as("JPAConfig should have @EntityScan annotation")
            .isTrue();
    }

    @Test
    void jpa_config_has_enable_jpa_repositories() {
        assertThat(JPAConfig.class.isAnnotationPresent(org.springframework.data.jpa.repository.config.EnableJpaRepositories.class))
            .as("JPAConfig should have @EnableJpaRepositories annotation")
            .isTrue();
    }
}