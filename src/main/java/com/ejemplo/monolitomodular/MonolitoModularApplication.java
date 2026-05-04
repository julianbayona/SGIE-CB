package com.ejemplo.monolitomodular;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MonolitoModularApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonolitoModularApplication.class, args);
    }
}
