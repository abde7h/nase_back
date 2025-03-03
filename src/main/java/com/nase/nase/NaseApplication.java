package com.nase.nase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.nase"})
@EntityScan("com.nase.model")
@EnableJpaRepositories("com.nase.repository")
public class NaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(NaseApplication.class, args);
	}

}
