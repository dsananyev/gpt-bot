package com.dsa;

import com.dsa.util.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final static PropertiesLoader loader = new PropertiesLoader();

    private final static String BOT_USERNAME = loader.getProperty("BOT_USERNAME");
    private final static String BOT_TOKEN = loader.getProperty("BOT_TOKEN");

    private static final Logger log = LoggerFactory.getLogger(Bot.class);

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            log.info(userMessage);
            String reply = HttpClient.sendMessage(userMessage);

            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(reply);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        log.info("BOT_USERNAME " + BOT_USERNAME);
        return BOT_USERNAME;
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }
}
