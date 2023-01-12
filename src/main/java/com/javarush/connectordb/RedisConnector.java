package com.javarush.connectordb;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;

public class RedisConnector {
    private static RedisConnector redisConnector;

    private final RedisClient redisClient;

    private RedisConnector() {
        redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            System.out.println("\nConnected to Redis\n");
        }
    }

    public static RedisClient getRedisClient() {
        if (redisConnector == null) {
            redisConnector = new RedisConnector();
        }

        return redisConnector.redisClient;
    }
}
