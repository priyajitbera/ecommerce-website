package com.priyajit.ecommerce.cart.service.config;

import com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import static com.priyajit.ecommerce.cart.service.mogodoc.DbEnvironmentConfiguration.Keys;

@Configuration
@EnableRedisRepositories(basePackages = "com.priyajit.ecommerce.cart.service.redisrepository")
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(DbEnvironmentConfiguration configuration) {
        String HOSTNAME = configuration.getProperty(Keys.REDIS_HOSTNAME);
        int PORT = Integer.parseInt(configuration.getProperty(Keys.REDIS_PORT));
        return new JedisConnectionFactory(
                new RedisStandaloneConfiguration(
                        HOSTNAME,
                        PORT
                )
        );
    }
}
