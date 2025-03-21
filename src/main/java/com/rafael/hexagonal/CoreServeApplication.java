package com.rafael.hexagonal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.rafael.hexagonal.infrastructure.adapter.output")
public class CoreServeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreServeApplication.class, args);
	}

}
