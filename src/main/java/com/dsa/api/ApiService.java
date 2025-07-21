package com.dsa.api;


import com.dsa.util.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.concurrent.TimeUnit;


@Slf4j
public class ApiService {

    private final OkHttpClient httpClient;
    private final static PropertiesLoader loader = new PropertiesLoader();
    private static final long CONNECT_TIMEOUT_SECONDS = Long.parseLong(loader.getProperty("CONNECT_TIMEOUT"));
    private static final long READ_TIMEOUT_SECONDS = Long.parseLong(loader.getProperty("READ_TIMEOUT"));
    private static final long WRITE_TIMEOUT_SECONDS = Long.parseLong(loader.getProperty("WRITE_TIMEOUT"));

    public ApiService() {
        httpClient = new OkHttpClient.Builder()
                        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                         .build();
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public Request createRequest(ApiProvider provider, String body) {
        return createRequestBuilder(provider)
                .post(RequestBody.create(body, MediaType.parse(provider.getContentType())))
                .build();
    }


    private Request.Builder createRequestBuilder(ApiProvider provider) {
        log.info("Created Request.Builder for ApiProvider: {}", provider.name());
        return new Request.Builder()
                .url(provider.getUrl() + provider.getEndpoint())
                .addHeader("Authorization", "Bearer " + provider.getToken())
                .addHeader("Content-Type", provider.getContentType());
    }


}
