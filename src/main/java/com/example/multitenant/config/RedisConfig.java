package com.example.multitenant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.*;


@Configuration(proxyBeanMethods = false)
@EnableRedisIndexedHttpSession(redisNamespace = "spring:sessions:java-app")
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.port}")
    private Integer port;

    @Value("${spring.data.redis.ssl.enabled}")
    private boolean isSsl;

    @Bean
    LettuceConnectionFactory connectionFactory() {
        var config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(password);
        
        return new LettuceConnectionFactory(config);
    }

    @Bean
    ProxyManager<String> lettuceBasedProxyManager() {
        RedisClient redisClient = redisClient();
        StatefulRedisConnection<String, byte[]> redisStatefulConnection = redisClient
            .connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

        return Bucket4jLettuce.casBasedBuilder(redisStatefulConnection)
            .build();
    }
    
    RedisClient redisClient() {
        return RedisClient.create(RedisURI.builder()
            .withHost(host)
            .withPort(port)
            .withSsl(isSsl)
            .build());
    }

    @Bean
    RedisTemplate<String, Object> generalRedisTemplate(RedisConnectionFactory connectionFactory) {
        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        // template.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        return template;
    }

    @Bean
    RedisTemplate<String, Long> counterRedisTemplate(RedisConnectionFactory connectionFactory) {
        var template = new RedisTemplate<String, Long>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Long.class));

        return template;
    }
}
