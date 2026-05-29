package com.example.core3;

import com.example.core3.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class Core3Application {

    public static void main(String[] args) {
        SpringApplication.run(Core3Application.class, args);
    }
}
