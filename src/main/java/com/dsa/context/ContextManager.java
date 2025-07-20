package com.dsa.context;

import com.dsa.api.ApiProvider;
import com.dsa.dto.open_ai.Message;
import com.dsa.util.JsonHelper;
import com.dsa.util.PropertiesLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
public class ContextManager {
    private static final String KEY_PREFIX = "chat::";
    private static final PropertiesLoader loader = new PropertiesLoader();
    private final long maxMessages = Long.parseLong(loader.getProperty("REDIS_MAX_MESSAGES"));
    private final long ttlSeconds = Long.parseLong(loader.getProperty("REDIS_TTL_SECONDS"));

    private static final ObjectMapper mapper = new ObjectMapper();

    public void addUserMessage(ApiProvider provider, long userId, String message) {
        addMessage(provider, userId, new Message("user", message));
    }

    public void addBotMessage(ApiProvider provider, long userId, String message) {
        addMessage(provider, userId, new Message("assistant", message));
    }


    public List<Message> getContext(ApiProvider provider, long userId) {
        try (var jedisPool = RedisManager.getJedis()) {
            var key = getKey(provider, userId);
            var list = jedisPool.lrange(key, 0, -1);

            return list.stream()
                    .map(json -> {
                        try {
                            return mapper.readValue(json, Message.class);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            log.error("Failed to get messages context by key: {}", getKey(provider, userId));
            return null;
        }
    }



    private void addMessage(ApiProvider provider, long userId, Message message) {
        try (var jedis = RedisManager.getJedis()) {
            var key = getKey(provider, userId);
            var json = JsonHelper.convertToJsonString(message);

            jedis.rpush(key, json);
            log.info("Added message to context for user: " + userId);
            jedis.ltrim(key, -maxMessages, -1);
            jedis.expire(key, ttlSeconds);
        } catch (Exception e) {
            log.error("Failed to add message to context");
        }
    }

    private String getKey(ApiProvider provider, long userId) {
        return KEY_PREFIX + provider.name() + "_" + userId;
    }





}
