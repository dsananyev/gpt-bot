package com.dsa;
import com.dsa.bot.Bot;
import com.dsa.context.RedisManager;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Slf4j
public class Main {
    public static void main(String[] args) {

        RedisManager.createRedisPool();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                RedisManager.jedisPoolClose();
                log.info("Redis connection closed successfully");
            } catch (Exception e) {
                log.error("Failed to close Redis connection: {}", e.getMessage());
            }
                log.info("Bot stopped successfully");
        }));

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot());
            log.info("Bot started successfully");
        } catch (TelegramApiException e) {
            e.printStackTrace();

        }
    }
}