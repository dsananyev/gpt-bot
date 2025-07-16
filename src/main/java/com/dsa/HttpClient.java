package com.dsa;

import com.dsa.util.PropertiesLoader;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Slf4j
@RequiredArgsConstructor
public class HttpClient {

    private final static PropertiesLoader loader = new PropertiesLoader();

    private static final String API_KEY = loader.getProperty("OPENAI_API_KEY");
    private static final String BASE_URI = loader.getProperty("OPENAI_BASE_URI");
    private static final String ENDPOINT = loader.getProperty("OPENAI_ENDPOINT");

    private static final Gson gson = new Gson();
    private static final Logger log = LoggerFactory.getLogger(Bot.class);



    public static String sendMessage(String message) {
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
