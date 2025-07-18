package com.dsa.context;

import com.dsa.util.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
public class RedisManager {

    private final static PropertiesLoader loader = new PropertiesLoader();

    private final static String REDIS_HOST = loader.getProperty("REDIS_HOST");
    private final static int REDIS_PORT = Integer.parseInt(loader.getProperty("REDIS_PORT"));

    private static JedisPool jedisPool;

    public static void createRedisPool() {
        if (jedisPool == null) {
            jedisPool = new JedisPool(REDIS_HOST, REDIS_PORT);
            log.info("JedisPool created at host:port {}:{}", REDIS_HOST, REDIS_PORT);
        }
    }

    public static Jedis getJedis() {
        if (jedisPool == null) {
            throw new IllegalStateException("JedisPool isn't created");
        }
        return jedisPool.getResource();
    }

    public static void jedisPoolClose() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }
}
