package com.dsa.bot;

import com.dsa.api.HttpClient;
import com.dsa.dto.Message;
import com.dsa.util.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Slf4j
public class Bot extends TelegramLongPollingBot {


    private static final PropertiesLoader loader = new PropertiesLoader();
    private static final HttpClient httpClient = new HttpClient();

    private static final String BOT_USERNAME = loader.getProperty("BOT_USERNAME");
    private static final String BOT_TOKEN = loader.getProperty("BOT_TOKEN");

    public Bot() {
        super(new DefaultBotOptions(), BOT_TOKEN);
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;

        var message = update.getMessage();
        var chatId = message.getChatId();

        try {
            if (message.hasText()) {
                proceedMessage(chatId, message.getText());
            } else if (message.hasPhoto()) {
                proceedMessage(chatId, message.getPhoto(), message.getCaption());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing message", e);
        }

    }


    private void proceedMessage(Long chatId, String message) {
        var reply = httpClient.sendMessage(chatId, message);
        sendReply(chatId, reply);
    }

    private void proceedMessage(Long chatId, List<PhotoSize> photoSizes, String caption) {

        var largestPhoto = photoSizes.stream()
                .max((photo1, photo2) -> Integer.compare(photo1.getHeight() * photo1.getWidth(), photo2.getHeight() * photo2.getWidth()));

        if (largestPhoto.isEmpty()) return;

        var fileUrl = getFileUrl(largestPhoto.get().getFileId());

        if (fileUrl != null) {
            var reply = httpClient.sendMessage(chatId, fileUrl, caption);
            sendReply(chatId, reply);
        } else {
            sendReply(chatId, "Error receiving image");
        }
    }



    private void sendReply(Long chatId, String reply) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(reply);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Error sending reply");
        }

    }


    private String getFileUrl(String fileId) {
        try {
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(fileId);
            File file = execute(getFileMethod);

            return "https://api.telegram.org/file/bot" + BOT_TOKEN + "/" + file.getFilePath();
        } catch (TelegramApiException e) {
            log.error("Ошибка при получении файла", e);
            return null;
        }
    }

    @Override
    public String getBotUsername() {
        log.info("BOT_USERNAME: {}", BOT_USERNAME);
        return BOT_USERNAME;
    }
}
