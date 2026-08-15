package com.bankflow.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.bankflow")
@EntityScan("com.bankflow")
public class JPAConfig { }