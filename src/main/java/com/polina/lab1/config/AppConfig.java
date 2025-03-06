package com.polina.lab1.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Scanner;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.polina.lab1")
@EnableTransactionManagement
public class AppConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public EntityManager entityManager() {
        return entityManager;
    }

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
}
