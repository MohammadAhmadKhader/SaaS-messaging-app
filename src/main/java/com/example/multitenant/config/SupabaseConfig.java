package com.example.multitenant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Getter
@Configuration
public class SupabaseConfig {
    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.jwt_secret}")
    private String jwtSecret;

    @Value("${supabase.storage.bucket}")
    private String bucketName;
}
