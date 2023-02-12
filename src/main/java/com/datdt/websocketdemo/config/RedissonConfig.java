package com.datdt.websocketdemo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "spring.redis", name = "enabled", havingValue = "true")
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${spring.redis.timeout:86400}")
    private int connectionTimeout;

    @Value("${spring.redis.connection-pool-size:64}")
    private int connectionPoolSize;

    @Bean
    public RedissonClient redisConfig() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setConnectionPoolSize(connectionPoolSize)
                .setConnectionMinimumIdleSize(5)
                .setConnectTimeout(connectionTimeout);

        return Redisson.create(config);
    }
}
