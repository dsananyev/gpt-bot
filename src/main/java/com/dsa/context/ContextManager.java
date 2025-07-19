package com.dsa.context;

import com.dsa.dto.Message;
import com.dsa.util.JsonHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class ContextManager {
    private static final String KEY_PREFIX = "chat::";
    private final long maxMessages = 10;
    private final long ttlSeconds = 3600;

    public void addUserMessage(long userId, String message) {
        addMessage(userId, new Message("user", message));
    }

    public void addBotMessage(long userId, String message) {
        addMessage(userId, new Message("assistant", message));
    }

//  public List<Message> getUserContext(long userId) {
//        try (var jedis = RedisManager.getJedis()) {
//            String key = KEY_PREFIX + userId;
//            List<String> list = jedis.lrange(key, 0, -1);
//        }
//    }



    private void addMessage(long userId, Message message) {
        try (var jedis = RedisManager.getJedis()) {
            String key = KEY_PREFIX + userId;
            String json = JsonHelper.convertToJsonString(message);

            jedis.rpush(key, json);
            log.info("Added message to context for user: " + userId);
            jedis.ltrim(key, -maxMessages, -1);
            jedis.expire(key, ttlSeconds);
        } catch (Exception e) {
            log.error("Failed to add message to context");
        }
    }





}
