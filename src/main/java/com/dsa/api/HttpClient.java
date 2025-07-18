package com.dsa.api;

import com.dsa.context.ContextManager;
import com.dsa.util.PropertiesLoader;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Slf4j
@RequiredArgsConstructor
public class HttpClient {

    private final static PropertiesLoader loader = new PropertiesLoader();
    private static final Gson gson = new Gson();
    private static final ContextManager contextManager = new ContextManager();

    private static final String API_KEY = loader.getProperty("OPENAI_API_KEY");
    private static final String BASE_URI = loader.getProperty("OPENAI_BASE_URI");
    private static final String ENDPOINT = loader.getProperty("OPENAI_ENDPOINT");

    public String sendMessage(long userId, String message) {
        contextManager.addUserMessage(userId, message);
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", message));

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o");
        body.put("messages", messages);

        RequestSpecification requestSpecBuilder = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setBasePath(ENDPOINT)
                .setContentType("application/json")
                .addHeader("Authorization", "Bearer "+ API_KEY)
                .build();

        var response = given()
                       .spec(requestSpecBuilder)
                .body(gson.toJson(body))
                .when()
                .post()
                .then()
                .statusCode(200);


        var extractedResponse = response.extract().path("choices[0].message.content");
        contextManager.addBotMessage(userId, extractedResponse.toString());

        return extractedResponse.toString();
    }

    public String sendMessage(String fileUrl, String caption) {
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

        RequestSpecification requestSpecBuilder = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setBasePath(ENDPOINT)
                .setContentType("application/json")
                .addHeader("Authorization", "Bearer "+ API_KEY)
                .log(LogDetail.ALL)
                .build();

        var response = given()
                .spec(requestSpecBuilder)
                .body(gson.toJson(body))
                .when()
                .post()
                .then()
                .statusCode(200);

        log.info(response.toString());

        var extractedResponse = response.extract().path("choices[0].message.content");

        return extractedResponse.toString();
    }
}
