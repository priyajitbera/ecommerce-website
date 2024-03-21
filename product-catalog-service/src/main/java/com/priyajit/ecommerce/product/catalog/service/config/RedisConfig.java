package com.priyajit.ecommerce.product.catalog.service.config;

import com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import static com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration.Keys;

@Configuration
@EnableRedisRepositories(basePackages = "com.priyajit.ecommerce.product.catalog.service.redisrepository")
public class RedisConfig {

    @Bean
    public RedisConnectionFactory connectionFactory(DbEnvironmentConfiguration configuration) {

        String HOSTNAME = configuration.getProperty(Keys.REDIS_HOSTNAME);
        int PORT = Integer.valueOf(configuration.getProperty(Keys.REDIS_PORT));
        return new JedisConnectionFactory(
                new RedisStandaloneConfiguration(
                        HOSTNAME,
                        PORT
                )
        );
    }
}
