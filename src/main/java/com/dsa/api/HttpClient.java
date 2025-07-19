package com.dsa.api;

import com.dsa.context.ContextManager;
import com.dsa.util.PropertiesLoader;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Slf4j
@RequiredArgsConstructor
public class HttpClient {

    private final static PropertiesLoader loader = new PropertiesLoader();
    private static final Gson gson = new Gson();
    private static final ContextManager contextManager = new ContextManager();
    private static final ApiService apiService = new ApiService();


    public String sendMessage(long userId, String message) {
        //Processing context
        contextManager.addUserMessage(userId, message);
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", message));

        //Creating request body

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o");
        body.put("messages", messages);

        var jsonBody = gson.toJson(body);


        try (Response response = apiService.getHttpClient()
                .newCall(apiService.createRequest(ApiProvider.OPEN_AI, jsonBody))
                .execute()) {

            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();

                Map<?, ?> parsed = gson.fromJson(responseBody, Map.class);
                List<?> choices = (List<?>) parsed.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> messageObj = (Map<?, ?>) choice.get("message");
                    String content = (String) messageObj.get("content");

                    // Добавляем ответ бота в контекст
                    contextManager.addBotMessage(userId, content);
                    return content;
                }
            } else {
                // Логируем ошибку
                log.error("Запрос к OpenAI неуспешен: " + response.code());
            }

        } catch (IOException e) {
            log.error("Ошибка при отправке запроса в OpenAI", e);
        }

        return "Ошибка обработки запроса.";
    }

    public String sendMessage(long userId, String fileUrl, String caption) {
        Map<String, Object> imagePart = Map.of(
                "type", "image_url",
                "image_url", Map.of("url", fileUrl)
        );

        Map<String, Object> textPart = Map.of(
                "type", "text",
                "text", caption != null ? caption : "Что на изображении?"
        );

        List<Map<String, Object>> content = List.of(imagePart, textPart);

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", content
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-4o",
                "messages", List.of(message),
                "max_tokens", 1000
        );


        var response = given()
                .spec(apiService.createRequestSpec(ApiProvider.OPEN_AI))
                .body(gson.toJson(body))
                .when()
                .post()
                .then()
                .statusCode(200);

        var extractedResponse = response.extract().path("choices[0].message.content");
        log.info("Got response from AI");
        return extractedResponse.toString();


    }
}
