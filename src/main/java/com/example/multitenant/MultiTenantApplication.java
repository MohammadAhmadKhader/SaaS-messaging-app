package com.example.multitenant;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultiTenantApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(MultiTenantApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("|||\tApplication has started: "+ LocalDateTime.now() +"\t  |||\n\n\n");
	}
}
