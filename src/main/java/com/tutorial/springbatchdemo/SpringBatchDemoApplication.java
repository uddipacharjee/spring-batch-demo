package com.tutorial.springbatchdemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class SpringBatchDemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
