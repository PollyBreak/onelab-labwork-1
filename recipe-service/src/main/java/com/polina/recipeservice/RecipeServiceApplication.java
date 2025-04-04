package com.polina.recipeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.polina.recipeservice.client")
@EnableElasticsearchRepositories
@SpringBootApplication
public class RecipeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeServiceApplication.class, args);
    }

}
