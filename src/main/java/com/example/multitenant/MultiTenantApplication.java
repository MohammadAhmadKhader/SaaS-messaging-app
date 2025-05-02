package com.example.multitenant;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.multitenant.utils.dataloader.DataLoaderInitCaller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class MultiTenantApplication implements CommandLineRunner {
	private final DataLoaderInitCaller dataLoaderInitCaller;
	private boolean shouldSeed = false;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MultiTenantApplication.class);
		app.addInitializers(ctx -> {
            var argsList = List.of(args);
            if (argsList.contains("--seed")) {
                ctx.getBeanFactory().registerSingleton("shouldSeedFlag", true);
            } else {
                ctx.getBeanFactory().registerSingleton("shouldSeedFlag", false);
            }
        });

        app.run(args);
	}

	@Override
	public void run(String... args) {
		log.info("application has started at: "+ LocalDateTime.now());

        if (shouldSeed) {
			log.info("app is starting to seed...");
			var startTimeMs = Instant.now().toEpochMilli();

            dataLoaderInitCaller.init();

			var endTimeMs = Instant.now().toEpochMilli();
			log.info("app has finished seeding within {} ms", endTimeMs - startTimeMs);
        }
	}

	@Autowired
    public void setSeedFlag(@Value("#{shouldSeedFlag}") boolean seedFlag) {
        this.shouldSeed = seedFlag;
    }
}