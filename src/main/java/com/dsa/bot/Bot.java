package com.dsa.bot;

import com.dsa.HttpClient;
import com.dsa.util.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Slf4j
public class Bot extends TelegramLongPollingBot {


    private final static PropertiesLoader loader = new PropertiesLoader();
    private static final HttpClient httpClient = new HttpClient();

    private final static String BOT_USERNAME = loader.getProperty("BOT_USERNAME");
    private final static String BOT_TOKEN = loader.getProperty("BOT_TOKEN");


    @Override
    public void onUpdateReceived(Update update) {
        Message message = null;
        if (update.hasMessage()) {
            message = update.getMessage();
        }

        if (message.hasText()) {
            var userMessage = message.getText();
            var reply = httpClient.sendMessage(message.getChatId(), userMessage);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessage.setText(reply);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (message.hasPhoto()) {
            List<PhotoSize> photos = message.getPhoto();
            PhotoSize largestPhoto = photos.get(photos.size() - 1); // Берем самое большое
            String fileId = largestPhoto.getFileId();
            String caption = message.getCaption(); //
            String fileUrl = getFileUrl(fileId);
            String gptResponse = httpClient.sendMessage(fileUrl, caption);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setText(gptResponse);

            try {
                execute(sendMessage);
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

    private String getFileUrl(String fileId) {
        try {
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(fileId);
            File file = execute(getFileMethod);

            return "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();
        } catch (TelegramApiException e) {
            log.error("Ошибка при получении файла", e);
            return null;
        }
    }
}
